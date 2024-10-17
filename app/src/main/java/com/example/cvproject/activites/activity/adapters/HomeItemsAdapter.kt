package com.example.cvproject.activites.activity.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cvproject.activites.activity.dataclass.ItemDataClass
import cvproject.blinkit.R
import cvproject.blinkit.databinding.HomeListItemsBinding

class HomeItemsAdapter(private val context: Context, private val imageList: List<ItemDataClass>) :
    RecyclerView.Adapter<HomeItemsAdapter.HomeItemsViewHolder>() {

    class HomeItemsViewHolder(
        private val binding: HomeListItemsBinding, private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ItemDataClass) {
            Glide.with(context).load(item.imageUrl).into(binding.iv)
            val rupeeSymbol = context.getString(R.string.rupee_symbol)
            val displayText = "${item.name}\n( $rupeeSymbol${item.price} / ${item.quantity} )"
            binding.tv.text = displayText
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): HomeItemsViewHolder {
        val binding =
            HomeListItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeItemsViewHolder(binding, context)
    }

    override fun onBindViewHolder(holder: HomeItemsViewHolder, position: Int) {
        holder.bind(imageList[position])
    }

    override fun getItemCount(): Int {
        return imageList.size
    }
}