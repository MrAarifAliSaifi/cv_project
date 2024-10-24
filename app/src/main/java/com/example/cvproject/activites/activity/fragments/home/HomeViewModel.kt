package cvproject.blinkit.activites.activity.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cvproject.activites.activity.constant.BlinkitConstants
import com.example.cvproject.activites.activity.dataBase.BlinkitDao
import com.example.cvproject.activites.activity.dataBase.HomeItems
import com.example.cvproject.activites.activity.dataBase.SavedAddresses
import com.example.cvproject.activites.activity.dataclass.ItemDataClass
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class HomeViewModel(
    private val blinkitDao: BlinkitDao, private val firebaseDatabase: FirebaseDatabase
) : ViewModel() {

    private val _itemList = MutableLiveData<List<ItemDataClass>>()
    val itemList: LiveData<List<ItemDataClass>> get() = _itemList
    private val itemsList = mutableListOf<ItemDataClass>()

    private val _saveAddress = MutableLiveData<List<SavedAddresses>>()
    val saveAddress: LiveData<List<SavedAddresses>> get() = _saveAddress

    fun saveItemUrl(itemId: String) {
        viewModelScope.launch {
            blinkitDao.insertItemUrl(HomeItems(itemIdGeneratedFromFirebase = itemId))
        }
    }

    fun fetchItemsFromDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            itemsList.clear()
            val reference =
                firebaseDatabase.getReference("BlinkitItems").child("All").child("Items")
            reference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (itemSnapshot in snapshot.children) {
                        val item = itemSnapshot.getValue(ItemDataClass::class.java)
                        if (item != null) {
                            itemsList.add(item)
                        }
                    }
                    _itemList.postValue(itemsList)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("TAG", "onCancelled: " + error.message)
                }
            })
        }
    }

    fun insertAddress(fetchedAddress: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val normalizedItemName = fetchedAddress.trim().lowercase(Locale.getDefault())
            val similarItemCount = blinkitDao.getSimilarItemCount(normalizedItemName)
            if (similarItemCount == 0) {
                blinkitDao.insertAddress(SavedAddresses(address = fetchedAddress))
                // Fetch all saved addresses after inserting a new one to ensure LiveData is updated
                fetchAllSavedAddress()
            }
        }
    }

    fun fetchAllSavedAddress() {
        viewModelScope.launch(Dispatchers.IO) {
            val address = blinkitDao.getAllSavedAddresses()
            Log.e("TAG", "fetchAllSavedAddress: $address")
            _saveAddress.postValue(address)
        }
    }

    fun getCurrentAddress(): String? {
        return _saveAddress.value?.lastOrNull()?.address
    }

    fun setCurrentAddress(newAddress: String) {
        Prefs.putString(BlinkitConstants.LOCATION, newAddress)
        viewModelScope.launch(Dispatchers.IO) {
            val currentAddresses = _saveAddress.value?.toMutableList() ?: mutableListOf()
            currentAddresses.add(SavedAddresses(address = newAddress))
            _saveAddress.postValue(currentAddresses)
            fetchAllSavedAddress()
        }
    }
}