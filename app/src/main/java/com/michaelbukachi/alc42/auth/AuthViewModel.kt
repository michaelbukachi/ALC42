package com.michaelbukachi.alc42.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.michaelbukachi.alc42.R
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.michaelbukachi.alc42.SingleLiveEvent

class AuthViewModel: ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    val showMessage = SingleLiveEvent<Int>()
    val launchMain = SingleLiveEvent<Void>()

    fun googleSignIn(account: GoogleSignInAccount) = viewModelScope.launch {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        try {
            val result = auth.signInWithCredential(credential).await()
            showMessage.value = R.string.success
            launchMain.value = null
        } catch (e: FirebaseException) {
            showMessage.value = R.string.auth_failed
            Log.e(javaClass.name, "An error occurred", e)
        }
    }
}