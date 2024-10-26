package com.example.cvproject.activites.activity.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.basicmvvmapp.MainActivity
import com.example.cvproject.activites.activity.activity.CheckoutActivity
import com.example.cvproject.activites.activity.constant.BlinkitConstants
import com.example.cvproject.activites.activity.dataclass.ItemDataClass
import com.example.cvproject.activites.activity.utilities.Utils
import com.pixplicity.easyprefs.library.Prefs
import cvproject.blinkit.R
import cvproject.blinkit.activites.activity.ui.dashboard.CategoryFragment
import cvproject.blinkit.databinding.ListItemModernBinding
import kotlin.random.Random

class ItemAdapter(
    private val fragment: CategoryFragment,
    private var items: List<ItemDataClass>
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(private val binding: ListItemModernBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ItemDataClass) {
            binding.apply {
                tvItemName.text = "${item.name} (${item.quantity})"
                tvQuantity.text = "${item.quantity}"
                tvPrice.text = fragment.getString(R.string.rupee_symbol, item.price)

                val randomValue = Random.nextInt(30, 40)
                val originalPrice = item.price!!.toInt() + randomValue
                tvRealPrice.apply {
                    paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    text = "MRP$originalPrice"
                }

                val discountPercent = Utils.returnPercentage(item.price.toInt(), originalPrice)
                tvDiscount.text = "${discountPercent}${fragment.requireContext().getString(R.string.off)}"
                Glide.with(root.context).load(item.imageUrl).into(ivItemImage)

                val displayText = "${item.name}\n(${
                    fragment.getString(
                        R.string.rupee_symbol, item.price
                    )
                } / ${item.quantity})"
                tvAdd.setOnClickListener {
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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding =
            ListItemModernBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
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
