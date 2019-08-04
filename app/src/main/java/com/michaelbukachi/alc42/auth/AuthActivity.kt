package com.michaelbukachi.alc42.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.michaelbukachi.alc42.R
import com.michaelbukachi.alc42.main.UserActivity
import kotlinx.android.synthetic.main.activity_auth.*

class AuthActivity : AppCompatActivity() {

    private val RC_SIGN_IN = 1
    lateinit var gClient: GoogleSignInClient
    lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        setTitle(R.string.app_name)
        googleSignIn.setSize(SignInButton.SIZE_STANDARD)
        val googleText = googleSignIn.getChildAt(0) as TextView
        googleText.text = "Sign in with google"
        googleSignIn.setOnClickListener {
            signInWithGoogle()
        }
        emailSignIn.setOnClickListener {
            startActivity(Intent(this@AuthActivity, SignUpActivity::class.java))
        }
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        gClient = GoogleSignIn.getClient(this, gso)
        viewModel = ViewModelProviders.of(this)[AuthViewModel::class.java]
        viewModel.showMessage.observe(this, Observer {
            Snackbar.make(container, it, Snackbar.LENGTH_SHORT).show()
        })
        viewModel.launchMain.observe(this, Observer {
            startActivity(Intent(this@AuthActivity, UserActivity::class.java))
            finish()
        })
    }

    private fun signInWithGoogle() {
        progressBar.visibility = View.VISIBLE
        val signInIntent = gClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                viewModel.googleSignIn(account!!)
            } catch (e: ApiException) {
                progressBar.visibility = View.GONE
                Log.e(javaClass.name, "An error occurred", e)
                Snackbar.make(container, "Google sign in failed", Snackbar.LENGTH_SHORT).show()
            }
        }
    }


}
