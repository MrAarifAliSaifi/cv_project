package cvproject.blinkit.activites.activity.ui.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cvproject.activites.activity.dataclass.CategoryDataClass
import com.example.cvproject.activites.activity.dataclass.ItemDataClass
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CategoryViewModel : ViewModel() {

    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    private val _categoryItems = MutableLiveData<List<CategoryDataClass>>()
    val categoryItems: LiveData<List<CategoryDataClass>> get() = _categoryItems

    private val _allItems = MutableLiveData<List<ItemDataClass>>()

    private val _filteredItems = MutableLiveData<List<ItemDataClass>>()
    val filteredItems: LiveData<List<ItemDataClass>> get() = _filteredItems

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    init {
        loadCategoriesFromFirebase()
        loadItems()
    }

    private fun loadCategoriesFromFirebase() {
        _isLoading.postValue(true)
        val databaseReference = FirebaseDatabase.getInstance().getReference("BlinkitItems")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val categories = mutableListOf<CategoryDataClass>()
                for (categorySnapshot in dataSnapshot.children) {
                    val categoryName = categorySnapshot.key ?: continue
                    if (categoryName == "All") {
                        continue
                    }
                    val categoryInfoSnapshot = categorySnapshot.child("categoryInfo")
                    val categoryImageUrl =
                        categoryInfoSnapshot.child("categoryImageUrl").value.toString()

                    val itemsList = mutableListOf<ItemDataClass>()
                    for (itemSnapshot in categorySnapshot.child("items").children) {
                        val item = itemSnapshot.getValue(ItemDataClass::class.java)
                        if (item != null) {
                            itemsList.add(item)
                        }
                    }

                    val category = CategoryDataClass(
                        name = categoryName, categoryImageUrl = categoryImageUrl, items = itemsList
                    )
                    categories.add(category)
                }
                _categoryItems.postValue(categories)
                _isLoading.postValue(false)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                _isLoading.postValue(false)
                Log.w(
                    "CategoryViewModel", "loadCategories:onCancelled", databaseError.toException()
                )
            }
        })
    }

    private fun loadItems() {
        _isLoading.postValue(true)
        database.child("BlinkitItems").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val itemList = mutableListOf<ItemDataClass>()
                for (categorySnapshot in snapshot.children) {
                    val categoryName = categorySnapshot.key ?: continue
                    if (categoryName == "All") {
                        continue
                    }

                    // Get items from the current category
                    val itemsSnapshot = categorySnapshot.child("items")
                    for (itemSnapshot in itemsSnapshot.children) {
                        val item = itemSnapshot.getValue(ItemDataClass::class.java)
                        item?.let { itemList.add(it) }
                    }
                }
                _allItems.value = itemList
                _filteredItems.value = itemList
                _isLoading.postValue(false)
            }

            override fun onCancelled(error: DatabaseError) {
                _isLoading.postValue(false)
            }
        })
    }

    fun filterItemsByCategory(category: CategoryDataClass) {
        val filteredList = _allItems.value?.filter { item ->
            category.items.any { it.id == item.id }
        }
        _filteredItems.value = filteredList ?: emptyList()
    }
}
