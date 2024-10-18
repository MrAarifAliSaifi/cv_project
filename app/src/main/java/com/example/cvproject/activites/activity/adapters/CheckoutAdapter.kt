package com.example.cvproject.activites.activity.adapters

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cvproject.activites.activity.activity.CheckoutActivity
import com.example.cvproject.activites.activity.dataclass.ItemDataClass
import com.example.cvproject.activites.activity.utilities.Utils
import cvproject.blinkit.R
import cvproject.blinkit.databinding.ListItemCheckoutBinding
import kotlin.random.Random

class CheckoutAdapter(
    private val context: Context, private val itemList: MutableList<ItemDataClass>
) : RecyclerView.Adapter<CheckoutAdapter.CheckItemsViewHolder>() {

    class CheckItemsViewHolder(
        private val binding: ListItemCheckoutBinding, private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: ItemDataClass, updatePriceCallback: () -> Unit
        ) {
            binding.apply {
                textViewItemName.text =
                    "${item.name}\n${context.getString(R.string.quantity, item.quantity)}"
                tvDiscountedPrice.text = context.getString(R.string.rupee_symbol, item.price)
                Glide.with(context).load(item.imageUrl).into(imageViewItemImage)

                (context as? CheckoutActivity)?.findViewById<ConstraintLayout>(R.id.cl_place_order)?.setOnClickListener {
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

                minus.setOnClickListener {
                    if (quantity > 1) {
                        quantity--
                        textViewValue.text = quantity.toString()
                        item.quantity = quantity.toString()
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
        return CheckItemsViewHolder(binding, context)
    }

    override fun onBindViewHolder(holder: CheckItemsViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item) {
            calculateTotalPrice(itemList)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    private fun calculateTotalPrice(itemList: List<ItemDataClass>) {
        val totalPrice = itemList.sumOf {
            val price = it.price!!.toIntOrNull() ?: 0
            val quantity = it.quantity!!.toIntOrNull() ?: 0
            price * quantity
        }

        (context as? CheckoutActivity)?.findViewById<TextView>(R.id.tv_total_price)?.text =
            context.getString(
                R.string.rupee_symbol, totalPrice.toString()
            ) + "\n" + context.getString(R.string.total)
    }
}