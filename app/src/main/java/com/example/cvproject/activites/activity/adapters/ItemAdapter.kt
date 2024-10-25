package com.example.cvproject.activites.activity.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.basicmvvmapp.MainActivity
import com.example.cvproject.activites.activity.activity.CheckoutActivity
import com.example.cvproject.activites.activity.constant.BlinkitConstants
import com.example.cvproject.activites.activity.dataclass.ItemDataClass
import com.pixplicity.easyprefs.library.Prefs
import cvproject.blinkit.R
import cvproject.blinkit.activites.activity.ui.dashboard.CategoryFragment
import kotlin.random.Random

class ItemAdapter(
    private val fragment: CategoryFragment,
    private var items: List<ItemDataClass>
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: ItemDataClass) {

            itemView.findViewById<TextView>(R.id.tv_item_name).text =
                "${item.name} (${item.quantity})"

            itemView.findViewById<TextView>(R.id.tv_quantity).text = "${item.quantity}"

            itemView.findViewById<TextView>(R.id.tv_price).text =
                "${fragment.getString(R.string.rupee_symbol, item.price)}"

            val randomValue = Random.nextInt(10, 20)
            val originalPrice = item.price!!.toInt() + randomValue
            val realPrice = itemView.findViewById<TextView>(R.id.tv_real_price)
            realPrice.paintFlags = realPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            realPrice.text = "MRP" + originalPrice.toString()

            val imageView = itemView.findViewById<ImageView>(R.id.iv_item_image)
            Glide.with(itemView.context).load(item.imageUrl).into(imageView)
            val displayText = "${item.name}\n( ${
                fragment.getString(
                    R.string.rupee_symbol, item.price
                )
            } / ${item.quantity} )"
            itemView.findViewById<TextView>(R.id.tv_add).setOnClickListener {
                fragment.insertItemToDb(item.id!!)
                (fragment.requireContext() as MainActivity).showBottomNavAndCart()
                fragment.requireContext()
                    .startActivity(CheckoutActivity.getStartIntent(fragment.requireContext()))
                Prefs.putString(BlinkitConstants.SELECTED_ITEM_DETAILS, displayText)
                Prefs.putString(BlinkitConstants.SELECTED_ITEM_IMAGE_URL, item.imageUrl)
                Prefs.putBoolean(BlinkitConstants.IS_ITEM_ADDED, true)
            }
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
