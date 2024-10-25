package com.example.cvproject.activites.activity.viewmodeles

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cvproject.activites.activity.dataBase.BlinkitDao
import com.example.cvproject.activites.activity.dataBase.UserInfo
import kotlinx.coroutines.launch

class MainActivityVM(private  val userDao: BlinkitDao):ViewModel() {

    private  val _userInfo=MutableLiveData<UserInfo>()
    val userInfo:LiveData<UserInfo> get() = _userInfo

    fun fetchUserInfo() {
        viewModelScope.launch {
            val user = userDao.getUserInfo()
            _userInfo.postValue(user)
        }
    }
}