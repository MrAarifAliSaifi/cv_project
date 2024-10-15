package com.example.cvproject.activites.activity.adapters

import android.view.LayoutInflater
import android.view.View
import cvproject.blinkit.R
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cvproject.activites.activity.dataclass.HomeItem
import cvproject.blinkit.databinding.HomeListItemsBinding

class HomeItemsAdapter(private val imageList: List<HomeItem>) : RecyclerView.Adapter<HomeItemsAdapter.HomeItemsViewHolder>() {

    class HomeItemsViewHolder(private val binding: HomeListItemsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HomeItem) {
            binding.iv.setImageResource(item.imageResId)
            binding.tv.text = "${item.name}\nPrice Starts ${item.price}"
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): HomeItemsViewHolder {
        val binding = HomeListItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeItemsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeItemsViewHolder, position: Int) {
        holder.bind(imageList[position])
    }

    override fun getItemCount(): Int {
        return imageList.size
    }
}