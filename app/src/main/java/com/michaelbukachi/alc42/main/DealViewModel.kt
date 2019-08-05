package com.michaelbukachi.alc42.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.michaelbukachi.alc42.SingleLiveEvent
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class DealViewModel : ViewModel() {
    val showMessageInt = SingleLiveEvent<Int>()
    val showMessage = SingleLiveEvent<String>()
    val onNewDeal = SingleLiveEvent<TravelDeal>()
    private val db = FirebaseDatabase.getInstance()
    private val dealsRef = db.reference.child("traveldeals")

    fun fetchData() {
        dealsRef.addChildEventListener(object : ChildEventListener {

            override fun onCancelled(error: DatabaseError) {
                Log.e(javaClass.name, error.message, error.toException())
                showMessage.postValue(error.message)
            }

            override fun onChildMoved(snapshot: DataSnapshot, key: String?) {
            }

            override fun onChildChanged(snapshot: DataSnapshot, key: String?) {
            }

            override fun onChildAdded(snapshot: DataSnapshot, key: String?) {
                val deal = snapshot.getValue(TravelDeal::class.java)
                onNewDeal.postValue(deal)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }
        })
    }

    fun saveDeal(title: String, description: String, price: String) = viewModelScope.launch {
        val deal = TravelDeal(title = title, description = description, price = price, imageUrl = "")
        dealsRef.push().setValue(deal).await()

    }

}