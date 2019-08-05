package com.michaelbukachi.alc42.main

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.michaelbukachi.alc42.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.deal_list_item.view.*

class DealAdapter : RecyclerView.Adapter<DealAdapter.DealViewHolder>() {

    private val deals = ArrayList<TravelDeal>()
    private val dealImage: ImageView? = null

    fun updateData(newData: List<TravelDeal>) {
        deals.clear()
        deals.addAll(newData)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DealViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.deal_list_item, parent, false)
        return DealViewHolder(itemView)
    }

    override fun getItemCount(): Int = deals.size

    override fun onBindViewHolder(holder: DealViewHolder, position: Int) {
        val deal = deals[position]
        holder.bind(deal)
    }


    inner class DealViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val title: TextView = itemView.title
        val description: TextView = itemView.description
        val price: TextView = itemView.price
        val image: ImageView = itemView.image

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(deal: TravelDeal) {
            title.text = deal.title
            description.text = deal.description
            price.text = deal.price
            showImage(deal.imageUrl)
        }

        override fun onClick(view: View) {
            val position = adapterPosition
            val deal = deals[position]
            val intent = Intent(view.context, AdminActivity::class.java)
            intent.putExtra("deal", deal)
            view.context.startActivity(intent)
        }

        private fun showImage(url: String?) {
            url?.let {
                if (it.isNotEmpty()) {
                    Picasso.Builder(image.context)
                        .build()
                        .load(it)
                        .placeholder(R.drawable.placeholder)
                        .resize(80, 80)
                        .centerCrop()
                        .into(image)
                }

            }
        }
    }
}