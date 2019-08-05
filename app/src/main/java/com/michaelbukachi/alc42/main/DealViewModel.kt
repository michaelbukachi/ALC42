package com.michaelbukachi.alc42.main

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.michaelbukachi.alc42.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class DealViewModel : ViewModel() {
    val showMessageInt = SingleLiveEvent<Int>()
    val showMessage = SingleLiveEvent<String>()
    val onDeals = SingleLiveEvent<List<TravelDeal>>()
    val refreshMenu = SingleLiveEvent<Void>()
    val setImage = SingleLiveEvent<Pair<String, String>>()
    private val db = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val dealsRef = db.reference.child("traveldeals")
    private val adminsRef = db.reference.child("administrators")
    private val dealsStorageRef = storage.reference.child("deals_pictures")
    var isAdmin = false

    private val authListener = FirebaseAuth.AuthStateListener { p0 ->
        val user = p0.currentUser
        if (user != null) {
            Log.i(javaClass.name, "Checking if you are an admin")
            val adminRef = adminsRef.child(user.uid)
            adminRef.addChildEventListener(object : ChildEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                }

                override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                }

                override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                    isAdmin = true
                    refreshMenu.value = null
                    Log.i(javaClass.name, "You are an administrator")
                }

                override fun onChildRemoved(p0: DataSnapshot) {
                }

            })
        }
    }

    init {
        auth.addAuthStateListener(authListener)
    }

    override fun onCleared() {
        auth.removeAuthStateListener(authListener)
        super.onCleared()
    }

    fun fetchData() {
        dealsRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Log.e(javaClass.name, error.message, error.toException().cause)
                showMessage.postValue(error.message)
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChildren()) {
                    val deals = mutableListOf<TravelDeal>()
                    for (child in snapshot.children) {
                        val deal = child.getValue(TravelDeal::class.java)
                        deal!!.id = child.key
                        deals.add(deal)
                    }
                    onDeals.value = deals
                }
            }

        })
    }

    fun saveDeal(deal: TravelDeal) = viewModelScope.launch {
        if (deal.id == null) {
            dealsRef.push().setValue(deal).await()
        } else {
            dealsRef.child(deal.id!!).setValue(deal).await()
        }

    }

    fun deleteDeal(deal: TravelDeal) = viewModelScope.launch {
        try {
            dealsRef.child(deal.id!!).removeValue().await()
            deal.imageName?.let {
                if (it.isNotEmpty()) {
                    val imageRef = storage.reference.child(it)
                    imageRef.delete().await()
                }
            }
        } catch (e: FirebaseException) {
            Log.e(javaClass.name, "An error occurred", e)
        }

    }

    fun uploadDealImage(uri: Uri) = viewModelScope.launch(Dispatchers.IO) {
        val imageRef = dealsStorageRef.child(uri.lastPathSegment!!)
        try {
            val uploadTask = imageRef.putFile(uri)
            uploadTask.await()
            val imageUrl = imageRef.downloadUrl.await().toString()
            val pictureName = imageRef.path
            Log.d(javaClass.name, "Url: $imageUrl")
            Log.d(javaClass.name, "Name: $pictureName")
            setImage.postValue(Pair(imageUrl, pictureName))
        } catch (e: FirebaseException) {
            Log.e(javaClass.name, e.message, e)
            showMessage.postValue(e.message)
        }

    }

}