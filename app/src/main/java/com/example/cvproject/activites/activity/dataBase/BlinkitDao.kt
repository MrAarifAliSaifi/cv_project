package com.example.cvproject.activites.activity.dataBase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.google.firebase.auth.UserInfo

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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddress(address: SavedAddresses)

    @Query("SELECT * FROM savedAddressesTable")
    suspend fun getAllSavedAddresses(): List<SavedAddresses>

    // Check that similer address is not present
    @Query("SELECT COUNT(*) FROM savedAddressesTable WHERE LOWER(:name) LIKE '%' || LOWER(:name) || '%'")
    fun getSimilarItemCount(name: String): Int
    @Insert
    suspend fun insertUser(user: UserInfo)

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<UserInfo>

    @Query("SELECT * FROM users ORDER BY createdAt DESC LIMIT 1")
    suspend fun getUserInfo(): UserInfo?
}