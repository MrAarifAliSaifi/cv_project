package com.example.cvproject.activites.activity.dataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [HomeItems::class, SavedAddresses::class, UserInfo::class], version = 3)
abstract class BlinkitDatabase : RoomDatabase() {

    abstract fun blinkitDao(): BlinkitDao

    companion object {
        @Volatile
        private var INSTANCE: BlinkitDatabase? = null
        fun getDatabase(context: Context): BlinkitDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, BlinkitDatabase::class.java, "blinkitDatabase"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}