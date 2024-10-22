package cvproject.blinkit.activites.activity.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cvproject.activites.activity.dataBase.BlinkitDao
import com.example.cvproject.activites.activity.dataBase.HomeItems
import kotlinx.coroutines.launch

class HomeViewModel(private val homePageItemsDao: BlinkitDao) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Welcome Ashish Mohan !"
    }
    val text: LiveData<String> = _text

    fun saveItemUrl(itemId: String) {
        viewModelScope.launch {
            homePageItemsDao.insertItemUrl(HomeItems(itemIdGeneratedFromFirebase = itemId))
        }
    }
}