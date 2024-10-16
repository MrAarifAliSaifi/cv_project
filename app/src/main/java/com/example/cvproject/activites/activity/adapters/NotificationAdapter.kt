package com.example.cvproject.activites.activity.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cvproject.activites.activity.dataclass.ListItemNotification
import cvproject.blinkit.databinding.SettingsItemBinding

class NotificationAdapter(
    var context: Context,
    private var dataList: List<ListItemNotification>,
    private val onItemClick: (ListItemNotification) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(var binding: SettingsItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): NotificationAdapter.ItemViewHolder {
        val binding =
            SettingsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationAdapter.ItemViewHolder, position: Int) {
        val item = dataList[position]
        holder.binding.xItemName.text = item.text
        holder.binding.xImageView.setImageResource(item.icon)

        holder.binding.settingsItem.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}