package com.example.cvproject.activites.activity.viewmodeles

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cvproject.activites.activity.dataBase.BlinkitDao
import com.example.cvproject.activites.activity.dataBase.BlinkitDatabase
import com.example.cvproject.activites.activity.dataclass.ItemDataClass
import com.example.cvproject.activites.activity.utilities.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CheckoutActivityVM(application: Application) : AndroidViewModel(application) {

    private val blinkitDao: BlinkitDao

    init {
        blinkitDao = BlinkitDatabase.getDatabase(application).blinkitDao()
    }

    private val _itemUrls = MutableLiveData<List<ItemDataClass>>()
    val itemUrls: LiveData<List<ItemDataClass>> get() = _itemUrls

    fun fetchAllSavedItemUrlsAndDetails() {
        viewModelScope.launch(Dispatchers.IO) {
            val urls = blinkitDao.getAllSavedItemUrl()
            Utils.fetchItemDetailsByUrls(urls) { itemList ->
                _itemUrls.postValue(itemList)
            }
        }
    }

    fun deleteItemUrl(itemId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            // Fetch the item from the database using its ID
            val itemToDelete = blinkitDao.getItemById(itemId)
            itemToDelete?.let {
                // Delete the fetched item
                blinkitDao.deleteItemUrl(it)
                Log.e("TAG", "called2")
                // Fetch the updated list from the database after deletion
                val urls = blinkitDao.getAllSavedItemUrl()
                Utils.fetchItemDetailsByUrls(urls) { itemList ->
                    _itemUrls.postValue(itemList)
                    Log.e("TAG", "called3")
                }
            }
        }
    }
}