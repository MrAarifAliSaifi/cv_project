package com.example.cvproject.activites.activity.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cvproject.activites.activity.dataclass.CategoryDataClass
import cvproject.blinkit.R
import cvproject.blinkit.databinding.ListItemCategoryBinding
import de.hdodenhof.circleimageview.CircleImageView
import org.w3c.dom.Text

class CategoryAdapter(
    private var context: Context,
    private var categories: List<CategoryDataClass>,
    private val onCategoryClicked: (CategoryDataClass) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var selectedPosition: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ListItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.bind(category, position)
    }

    fun updateCategories(newCategories: List<CategoryDataClass>) {
        categories = newCategories
        notifyDataSetChanged()
    }

    inner class CategoryViewHolder(private val binding: ListItemCategoryBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(category: CategoryDataClass, position: Int) {
            binding.tvCtName.text = category.name
            Glide.with(binding.root.context).load(category.categoryImageUrl).into(binding.ivCtImage)

            // Set the visibility of the indicator
            binding.indicatorView.visibility = if (position == selectedPosition) View.VISIBLE else View.GONE

            // Set click listener for selecting category
            binding.root.setOnClickListener {
                selectItem(position)
                onCategoryClicked(category)
            }
        }
    }

    fun selectItem(position: Int) {
        notifyItemChanged(selectedPosition) // Reset previous selected item
        selectedPosition = position
        notifyItemChanged(selectedPosition) // Update new selected item
    }

    override fun getItemCount(): Int {
        return categories.size
    }
}