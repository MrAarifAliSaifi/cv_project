package cvproject.blinkit.activites.activity.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cvproject.activites.activity.dataBase.BlinkitDao
import com.example.cvproject.activites.activity.dataBase.UserInfo
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch

class SettingsViewModel(private val blinkitDao: BlinkitDao, private val firebaseDatabase: FirebaseDatabase) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is notifications Fragment"
    }
    val text: LiveData<String> = _text

    private  val _userInfo=MutableLiveData<UserInfo>()
    val userInfo:LiveData<UserInfo> get() = _userInfo
    fun fetchUserInfo() {
        viewModelScope.launch {
            val user = blinkitDao.getUserInfo()
            _userInfo.postValue(user)
        }
    }
}