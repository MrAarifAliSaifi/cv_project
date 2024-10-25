package com.example.cvproject.activites.activity.dataBase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserInfo(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val imageUri: String,
    val createdAt: Long = System.currentTimeMillis() // Default to current time

)