package com.example.cvproject.activites.activity.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.basicmvvmapp.MainActivity
import com.example.cvproject.activites.activity.activity.CheckoutActivity
import com.example.cvproject.activites.activity.constant.BlinkitConstants
import com.example.cvproject.activites.activity.dataclass.ItemDataClass
import com.pixplicity.easyprefs.library.Prefs
import cvproject.blinkit.R
import cvproject.blinkit.activites.activity.ui.home.HomeViewModel
import cvproject.blinkit.databinding.HomeListItemsBinding

class HomeItemsAdapter(
    private val context: Context,
    private val imageList: List<ItemDataClass>,
    private val homeViewModel: HomeViewModel
) : RecyclerView.Adapter<HomeItemsAdapter.HomeItemsViewHolder>() {

    class HomeItemsViewHolder(
        private val binding: HomeListItemsBinding,
        private val context: Context,
        private val homeViewModel: HomeViewModel
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ItemDataClass) {
            Glide.with(context).load(item.imageUrl).into(binding.iv)
            val displayText = "${item.name}\n( ${
                context.getString(
                    R.string.rupee_symbol, item.price
                )
            } / ${item.quantity} )"
            binding.tv.text = displayText
            binding.parent.setOnClickListener {
                context.startActivity(CheckoutActivity.getStartIntent(context))
                homeViewModel.saveItemUrl(item.id!!)
                Prefs.putString(BlinkitConstants.SELECTED_ITEM_DETAILS, displayText)
                Prefs.putString(BlinkitConstants.SELECTED_ITEM_IMAGE_URL, item.imageUrl)
                Prefs.putBoolean(BlinkitConstants.IS_ITEM_ADDED, true)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): HomeItemsViewHolder {
        val binding =
            HomeListItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeItemsViewHolder(binding, context, homeViewModel)
    }

    override fun onBindViewHolder(holder: HomeItemsViewHolder, position: Int) {
        holder.bind(imageList[position])
    }

    override fun getItemCount(): Int {
        return imageList.size
    }
}