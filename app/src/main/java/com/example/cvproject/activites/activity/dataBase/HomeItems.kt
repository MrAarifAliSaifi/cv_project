package com.example.cvproject.activites.activity.dataBase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "itemsId")
data class HomeItems(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, val itemIdGeneratedFromFirebase: String?
)
