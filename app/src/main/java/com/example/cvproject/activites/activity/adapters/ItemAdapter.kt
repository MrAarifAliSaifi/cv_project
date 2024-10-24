package com.example.cvproject.activites.activity.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cvproject.activites.activity.dataclass.ItemDataClass
import cvproject.blinkit.R

class ItemAdapter(private val context: Context, private var items: List<ItemDataClass>) :
    RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: ItemDataClass) {
            itemView.findViewById<TextView>(R.id.tv_item_name).text = item.name
            val displayText = "( ${
                context.getString(
                    R.string.rupee_symbol, item.price
                )
            } / ${item.quantity} )"
            itemView.findViewById<TextView>(R.id.tv_additional_details).text = displayText
            val imageView = itemView.findViewById<ImageView>(R.id.image_view)
            Glide.with(itemView.context).load(item.imageUrl).into(imageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_modern, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    fun updateItems(newItems: List<ItemDataClass>) {
        items = newItems
        notifyDataSetChanged()
    }
}
