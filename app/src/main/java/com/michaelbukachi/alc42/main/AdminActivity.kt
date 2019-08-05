package com.michaelbukachi.alc42.main

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
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
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_admin.*

class AdminActivity : AppCompatActivity() {

    private val PICTURE_RESULT = 42
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
        viewModel.refreshMenu.observe(this, Observer {
            invalidateOptionsMenu()
            txtTitle.isEnabled = viewModel.isAdmin
            txtDescription.isEnabled = viewModel.isAdmin
            txtPrice.isEnabled = viewModel.isAdmin
            selectImage.visibility = if (viewModel.isAdmin) View.VISIBLE else View.GONE
        })
        viewModel.setImage.observe(this, Observer {
            progressBar.visibility = View.GONE
            this.deal.imageUrl = it.first
            this.deal.imageName = it.second
            showImage(it.first)
        })
        val deal = intent.getSerializableExtra("deal") as TravelDeal?
        deal?.let {
            this.deal = it
        }
        txtTitle.setText(this.deal.title)
        txtDescription.setText(this.deal.description)
        txtPrice.setText(this.deal.price)
        showImage(this.deal.imageUrl)
        selectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/jpeg"
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            startActivityForResult(Intent.createChooser(intent, "Insert Picture"), PICTURE_RESULT)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.admin_menu, menu)
        menu.findItem(R.id.save).isVisible = viewModel.isAdmin
        menu.findItem(R.id.delete).isVisible = viewModel.isAdmin
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICTURE_RESULT && resultCode == Activity.RESULT_OK) {
            val imageUri = data!!.data
            progressBar.visibility = View.VISIBLE
            viewModel.uploadDealImage(imageUri!!)
        }
    }

    private fun saveDeal() {
        if (txtTitle.text.toString().isEmpty() || txtDescription.text.toString().isEmpty() ||
            txtPrice.text.toString().isEmpty()
        ) {
            Snackbar.make(container, R.string.fill_all_fields, Snackbar.LENGTH_SHORT).show()
            return
        }
        progressBar.visibility = View.VISIBLE
        this.deal.title = txtTitle.text.toString()
        this.deal.description = txtDescription.text.toString()
        this.deal.price = txtPrice.text.toString()
        viewModel.saveDeal(deal)
        Snackbar.make(container, "Deal Saved", Snackbar.LENGTH_SHORT).show()
        progressBar.visibility = View.GONE
        clear()
        backToList()
    }

    private fun deleteDeal() {
        if (deal.id.isNullOrEmpty()) {
            Snackbar.make(container, "Please save the deal before deleting", Snackbar.LENGTH_SHORT).show()
            return
        }
        viewModel.deleteDeal(deal)
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

    private fun showImage(url: String?) {
        url?.let {
            if (it.isNotEmpty()) {
                val width = Resources.getSystem().displayMetrics.widthPixels
                Picasso.get()
                    .load(it)
                    .resize(width, width * 2 / 3)
                    .centerCrop()
                    .into(dealImage)
            }
        }

    }
}
