package com.example.cvproject.activites.activity.dataBase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "savedAddressesTable")
data class SavedAddresses(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val address: String?
)
