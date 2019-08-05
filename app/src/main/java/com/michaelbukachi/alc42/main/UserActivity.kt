package com.michaelbukachi.alc42.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.michaelbukachi.alc42.R
import com.michaelbukachi.alc42.auth.AuthActivity
import kotlinx.android.synthetic.main.activity_user.*

class UserActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    lateinit var viewModel: DealViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        val adapter = DealAdapter()
        dealsList.adapter = adapter
        viewModel = ViewModelProviders.of(this)[DealViewModel::class.java]
        viewModel.showMessageInt.observe(this, Observer {
            progressBar.visibility = View.GONE
            Snackbar.make(container, it, Snackbar.LENGTH_SHORT).show()
        })
        viewModel.showMessage.observe(this, Observer {
            progressBar.visibility = View.GONE
            Snackbar.make(container, it, Snackbar.LENGTH_SHORT).show()
        })
        viewModel.onDeals.observe(this, Observer {
            blank.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
            progressBar.visibility = View.GONE
            adapter.updateData(it)
        })
    }

    override fun onStart() {
        super.onStart()
        viewModel.fetchData()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.user_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_deal -> {
                startActivity(Intent(this, AdminActivity::class.java))
                true
            }
            R.id.logout -> {
                auth.signOut()
                Toast.makeText(this, "Signing out...", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, AuthActivity::class.java))
                true
            }
            else -> false
        }
    }
}
