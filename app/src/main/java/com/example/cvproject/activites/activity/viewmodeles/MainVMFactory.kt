package com.example.cvproject.activites.activity.viewmodeles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cvproject.activites.activity.dataBase.BlinkitDao

class MainVMFactory(private val blinkitDao: BlinkitDao) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainActivityVM::class.java)) {
            return MainActivityVM(blinkitDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}