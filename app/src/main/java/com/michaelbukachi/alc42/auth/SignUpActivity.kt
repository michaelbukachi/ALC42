package com.michaelbukachi.alc42.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.michaelbukachi.alc42.R
import com.michaelbukachi.alc42.main.UserActivity
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_sign_up.container
import kotlinx.android.synthetic.main.activity_sign_up.progressBar

class SignUpActivity : AppCompatActivity() {
    lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        setTitle(R.string.sign_up)
        saveButton.setOnClickListener {
            if (email.text.toString().isEmpty() || name.text.toString().isEmpty() || password.text.toString().isEmpty()) {
                Snackbar.make(container, getString(R.string.fill_all_fields), Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE
            saveButton.isEnabled = false
            viewModel.signUp(email.text.toString(), password.text.toString(), name.text.toString())

        }
        viewModel = ViewModelProviders.of(this)[AuthViewModel::class.java]
        viewModel.showMessageInt.observe(this, Observer {
            progressBar.visibility = View.GONE
            saveButton.isEnabled = true
            Snackbar.make(container, it, Snackbar.LENGTH_SHORT).show()
        })
        viewModel.showMessage.observe(this, Observer {
            progressBar.visibility = View.GONE
            saveButton.isEnabled = true
            Snackbar.make(container, it, Snackbar.LENGTH_SHORT).show()
        })
        viewModel.launchMain.observe(this, Observer {
            val intent = Intent(this@SignUpActivity, UserActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        })
    }
}
