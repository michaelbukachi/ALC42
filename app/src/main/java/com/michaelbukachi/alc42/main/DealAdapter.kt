package com.michaelbukachi.alc42.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.michaelbukachi.alc42.R
import kotlinx.android.synthetic.main.deal_list_item.view.*

class DealAdapter : RecyclerView.Adapter<DealAdapter.DealViewHolder>() {
    private val deals = ArrayList<TravelDeal>()

    fun updateData(newData: List<TravelDeal>) {
        deals.clear()
        deals.addAll(newData)
        notifyDataSetChanged()
    }

    fun addDeal(deal: TravelDeal) {
        deals.add(deal)
        notifyItemInserted(deals.size - 1)
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


    class DealViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.title
        val description: TextView = itemView.description
        val price: TextView = itemView.price

        fun bind(deal: TravelDeal) {
            title.text = deal.title
            description.text = deal.description
            price.text = deal.price
        }
    }
}