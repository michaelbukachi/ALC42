package com.michaelbukachi.alc42.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.*
import com.michaelbukachi.alc42.R
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.michaelbukachi.alc42.SingleLiveEvent
import kotlinx.coroutines.Dispatchers

class AuthViewModel: ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    val showMessageInt = SingleLiveEvent<Int>()
    val showMessage = SingleLiveEvent<String>()
    val launchMain = SingleLiveEvent<Void>()

    fun isSignedIn(): Boolean = auth.currentUser != null

    fun googleSignIn(account: GoogleSignInAccount) = viewModelScope.launch(Dispatchers.IO) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        try {
            val result = auth.signInWithCredential(credential).await()
            if (result.additionalUserInfo.isNewUser) {
                setDisplayName(account.displayName)
            }
            showMessageInt.postValue(R.string.success)
            launchMain.postValue(null)
        } catch (e: FirebaseException) {
            Log.e(javaClass.name, "An error occurred", e)
            showMessage.postValue(getPrettyMessage(e))
        }
    }

    fun signUp(email: String, password: String, name: String) = viewModelScope.launch(Dispatchers.IO) {
        try {
            Log.i(javaClass.name, "Signing in...")
            auth.signInWithEmailAndPassword(email, password).await()
            showMessageInt.postValue(R.string.success)
            launchMain.postValue(null)
        } catch (e: FirebaseException) {
            if (e is FirebaseAuthInvalidUserException) {
                Log.i(javaClass.name, "Signing up...")
                auth.createUserWithEmailAndPassword(email, password).await()
                setDisplayName(name)
                showMessageInt.postValue(R.string.success)
                launchMain.postValue(null)
            } else {
                Log.e(javaClass.name, "An error occurred", e)
                showMessage.postValue(getPrettyMessage(e))
            }
        }
    }

    private suspend fun setDisplayName(displayName: String?) {
        val user = auth.currentUser!!
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(displayName)
            .build()
        user.updateProfile(profileUpdates).await()
    }

    private fun getPrettyMessage(e: FirebaseException): String {
        if (e is FirebaseNetworkException) {
            return "Unable to connect"
        } else if (e is FirebaseAuthInvalidUserException || e is FirebaseAuthInvalidCredentialsException) {
            return "Invalid credentials"
        } else if (e is FirebaseAuthUserCollisionException) {
            return "The email address is already in use"
        } else {
            return "An error has occurred"
        }
    }
}