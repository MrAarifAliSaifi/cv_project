package com.example.cvproject.activites.activity.dataBase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BlinkitDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItemUrl(homeItems: HomeItems)

    @Delete
    suspend fun deleteItemUrl(homeItems: HomeItems)

    @Query("SELECT * FROM itemsId")
    suspend fun getAllSavedItemUrl(): List<HomeItems>

    @Query("SELECT * FROM itemsId WHERE itemIdGeneratedFromFirebase = :itemId")
    suspend fun getItemById(itemId: String): HomeItems?

}