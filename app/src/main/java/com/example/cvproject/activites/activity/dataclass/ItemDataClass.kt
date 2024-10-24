package com.example.cvproject.activites.activity.dataclass

data class ItemDataClass(
    val id: String? = "",
    val name: String? = "",
    val price: String? = "",
    var quantity: String? = "",
    val imageUrl: String? = ""
)

data class CategoryDataClass(
    val name: String = "",
    val categoryImageUrl: String = "",
    val items: List<ItemDataClass> = emptyList()
)

