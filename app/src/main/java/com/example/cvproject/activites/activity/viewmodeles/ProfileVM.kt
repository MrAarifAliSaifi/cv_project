package com.example.cvproject.activites.activity.viewmodeles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cvproject.activites.activity.dataBase.BlinkitDao
import com.example.cvproject.activites.activity.dataBase.UserInfo
import kotlinx.coroutines.launch
class ProfileVM(private val homePageItemsDao: BlinkitDao) : ViewModel() {
    fun saveUserInfo(userName: String, imageUri: String) {
        val user = UserInfo(name = userName, imageUri = imageUri)
        viewModelScope.launch {
            homePageItemsDao.insertUser(user)
        }
    }
}