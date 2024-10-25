package com.example.cvproject.activites.activity.viewmodeles

import androidx.lifecycle.ViewModel
import com.example.cvproject.activites.activity.dataBase.BlinkitDao

class MainActivityVM(private val userDao: BlinkitDao) : ViewModel() {
    override fun onCleared() {
        super.onCleared()
    }
}