package com.michaelbukachi.alc42.main

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.michaelbukachi.alc42.R
import kotlinx.android.synthetic.main.activity_admin.*

class AdminActivity : AppCompatActivity() {

    lateinit var viewModel: DealViewModel
    private var deal = TravelDeal(title = "", description = "", price = "", imageUrl = "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        viewModel = ViewModelProviders.of(this)[DealViewModel::class.java]
        viewModel.showMessageInt.observe(this, Observer {
            Snackbar.make(container, it, Snackbar.LENGTH_SHORT).show()
        })
        viewModel.showMessage.observe(this, Observer {
            Snackbar.make(container, it, Snackbar.LENGTH_SHORT).show()
        })
        val deal = intent.getSerializableExtra("deal") as TravelDeal?
        deal?.let {
            this.deal = it
        }
        txtTitle.setText(this.deal.title)
        txtDescription.setText(this.deal.description)
        txtPrice.setText(this.deal.price)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.admin_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save -> {
                hideKeyboard()
                saveDeal()
                true
            }
            R.id.delete -> {
                hideKeyboard()
                deleteDeal()
                true
            }
            else -> false
        }
    }

    private fun saveDeal() {
        if (txtTitle.text.toString().isEmpty() || txtDescription.text.toString().isEmpty() ||
            txtPrice.text.toString().isEmpty()
        ) {
            Snackbar.make(container, R.string.fill_all_fields, Snackbar.LENGTH_SHORT).show()
            return
        }
        this.deal.title = txtTitle.text.toString()
        this.deal.description = txtDescription.text.toString()
        this.deal.price = txtPrice.text.toString()
        viewModel.saveDeal(deal)
        Snackbar.make(container, "Deal Saved", Snackbar.LENGTH_SHORT).show()
        clear()
        backToList()
    }

    private fun deleteDeal() {
        if (deal.id.isNullOrEmpty()) {
            Snackbar.make(container, "Please save the deal before deleting", Snackbar.LENGTH_SHORT).show()
            return
        }
        viewModel.deleteDeal(deal.id!!)
        backToList()
    }

    private fun backToList() {
        onBackPressed()
    }

    private fun clear() {
        txtTitle.setText("")
        txtDescription.setText("")
        txtPrice.setText("")
        txtTitle.requestFocus()
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = currentFocus
        if (view == null) {
            view = View(this)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
