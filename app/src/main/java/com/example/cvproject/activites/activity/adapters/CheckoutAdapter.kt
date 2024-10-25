package com.example.cvproject.activites.activity.adapters

import android.content.Context
import android.graphics.Paint
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cvproject.activites.activity.activity.CheckoutActivity
import com.example.cvproject.activites.activity.constant.BlinkitConstants
import com.example.cvproject.activites.activity.dataclass.ItemDataClass
import com.example.cvproject.activites.activity.utilities.Utils
import com.example.cvproject.activites.activity.viewmodeles.CheckoutActivityVM
import com.pixplicity.easyprefs.library.Prefs
import cvproject.blinkit.R
import cvproject.blinkit.databinding.ListItemCheckoutBinding
import kotlin.random.Random

class CheckoutAdapter(
    private val context: Context,
    private val itemList: MutableList<ItemDataClass>,
    private val checkoutViewModel: CheckoutActivityVM
) : RecyclerView.Adapter<CheckoutAdapter.CheckItemsViewHolder>() {

    class CheckItemsViewHolder(
        private val binding: ListItemCheckoutBinding,
        private val context: Context,
        private val checkoutViewModel: CheckoutActivityVM
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: ItemDataClass,
            itemList: MutableList<ItemDataClass>,
            updatePriceCallback: () -> Unit,
            removeItemCallback: (Int) -> Unit
        ) {
            binding.apply {
                textViewItemName.text = TextUtils.concat(
                    Utils.styleStrings(item.name!!),
                    "\n",
                    context.getString(R.string.quantity, item.quantity)
                )
                tvDiscountedPrice.text = " ${context.getString(R.string.rupee_symbol, item.price)}"
                Glide.with(context).load(item.imageUrl).into(imageViewItemImage)

                (context as? CheckoutActivity)?.findViewById<ConstraintLayout>(R.id.cl_place_order)
                    ?.setOnClickListener {
                        Utils.showToast(context, "Yet to be Implemented")
                    }

                (context as? CheckoutActivity)?.findViewById<TextView>(R.id.tv_total_price)?.text =
                    context.getString(
                        R.string.rupee_symbol, item.price
                    ) + "\n" + context.getString(R.string.total)
                tvRealPrice.paintFlags = tvRealPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                val randomValue = Random.nextInt(10, 20)
                val originalPrice = item.price!!.toInt() + randomValue
                tvRealPrice.text =
                    context.getString(R.string.rupee_symbol, originalPrice.toString())
                var quantity = item.quantity!!.toIntOrNull() ?: 1
                textViewValue.text = quantity.toString()

                buttonMinus.setOnClickListener {
                    if (quantity > 1) {
                        quantity--
                        textViewValue.text = quantity.toString()
                        item.quantity = quantity.toString()
                        updatePriceCallback()
                    } else if (quantity == 1) {
                        checkoutViewModel.deleteItemUrl(item.id!!)
                        removeItemCallback(adapterPosition)
                        updatePriceCallback()
                    }
                }

                buttonPlus.setOnClickListener {
                    quantity++
                    textViewValue.text = quantity.toString()
                    item.quantity = quantity.toString()
                    updatePriceCallback()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckItemsViewHolder {
        val binding =
            ListItemCheckoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CheckItemsViewHolder(binding, context, checkoutViewModel)
    }

    override fun onBindViewHolder(holder: CheckItemsViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item, itemList, {
            calculateTotalPrice(itemList)
        }) { position ->
            // Remove the item from the list and notify the adapter
            itemList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, itemList.size)
            if (itemList.isEmpty()) {
                (context as CheckoutActivity).finish()
            }
        }

        if (itemList.size == 0) {
            Prefs.remove(BlinkitConstants.SELECTED_ITEM_DETAILS)
            Prefs.remove(BlinkitConstants.SELECTED_ITEM_IMAGE_URL)
            Prefs.remove(BlinkitConstants.IS_ITEM_ADDED)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun calculateTotalPrice(itemList: List<ItemDataClass>) {
        val totalPrice = itemList.sumOf {
            val price = it.price!!.toIntOrNull() ?: 0
            val quantity = it.quantity!!.toIntOrNull() ?: 1
            Log.e("TAG", "Price " + it.price + "qty " + it.quantity)
            price * quantity
        }
        Log.e("TAG", "calculateTotalPrice: Adapter" + totalPrice)
        (context as? CheckoutActivity)?.findViewById<TextView>(R.id.tv_total_price)?.text =
            context.getString(
                R.string.rupee_symbol, totalPrice.toString()
            ) + "\n" + context.getString(R.string.total)
    }
}