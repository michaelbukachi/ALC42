package com.michaelbukachi.alc42.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.michaelbukachi.alc42.SingleLiveEvent
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class DealViewModel : ViewModel() {
    val showMessageInt = SingleLiveEvent<Int>()
    val showMessage = SingleLiveEvent<String>()
    val onDeals = SingleLiveEvent<List<TravelDeal>>()
    private val db = FirebaseDatabase.getInstance()
    private val dealsRef = db.reference.child("traveldeals")

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

    fun deleteDeal(id: String) = viewModelScope.launch {
        dealsRef.child(id).removeValue().await()
    }

}