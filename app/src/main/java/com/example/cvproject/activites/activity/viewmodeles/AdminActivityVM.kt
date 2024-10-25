package com.example.cvproject.activites.activity.viewmodeles

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cvproject.activites.activity.constant.BlinkitConstants
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class AdminActivityVM : ViewModel() {

    private val _uploadStatus = MutableLiveData<String>()
    val uploadStatus: LiveData<String> get() = _uploadStatus

    fun uploadItemToFirebase(
        itemName: String,
        itemPrice: String,
        itemQuantity: String,
        selectedImageUri: Uri?,
        categoryName: String,
        categoryImageUri: Uri?
    ) {
        _uploadStatus.value = BlinkitConstants.UPLOADING
        val storageReference = FirebaseStorage.getInstance().reference
        val imageRef = storageReference.child("Images/${UUID.randomUUID()}.jpg")

        selectedImageUri?.let { uri ->
            imageRef.putFile(uri).addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    uploadCategoryImage(categoryImageUri, categoryName) { categoryImageUrl ->
                        saveItemDataToDatabase(
                            itemName,
                            itemPrice,
                            itemQuantity,
                            downloadUri.toString(),
                            categoryName,
                            categoryImageUrl
                        )
                    }
                }
            }.addOnFailureListener {
                _uploadStatus.value = BlinkitConstants.FAILED_TO_UPLOAD_IMAGE
            }
        } ?: run {
            _uploadStatus.value = BlinkitConstants.NO_IMAGE_SELECTED
        }
    }

    private fun uploadCategoryImage(
        categoryImageUri: Uri?, categoryName: String, onSuccess: (String) -> Unit
    ) {
        if (categoryImageUri == null) {
            onSuccess("")
            return
        }

        val storageReference = FirebaseStorage.getInstance().reference
        val categoryImageRef =
            storageReference.child("CategoryImages/${categoryName}_${UUID.randomUUID()}.jpg")

        categoryImageRef.putFile(categoryImageUri).addOnSuccessListener {
            categoryImageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                onSuccess(downloadUri.toString())
            }
        }.addOnFailureListener {
            _uploadStatus.value = BlinkitConstants.FAILED_TO_UPLOAD_CATEGORY_IMAGE
        }
    }

    private fun saveItemDataToDatabase(
        name: String,
        price: String,
        quantity: String,
        imageUrl: String,
        categoryName: String,
        categoryImageUrl: String
    ) {
        val databaseReference = FirebaseDatabase.getInstance().reference.child("BlinkitItems")
        val itemId = databaseReference.push().key

        // Create item data without categoryImageUrl
        val itemData = mapOf(
            "id" to itemId,
            "name" to name,
            "price" to price,
            "quantity" to quantity,
            "imageUrl" to imageUrl,
        )

        // Reference to the category
        val categoryRef = databaseReference.child(categoryName)

        // Create a category object with name and image URL
        val categoryData = mapOf(
            "name" to categoryName,
            "categoryImageUrl" to categoryImageUrl
        )

        // Update category image URL and item data in the category node
        categoryRef.child("categoryInfo").setValue(categoryData).addOnSuccessListener {
            // Save the item under the category
            categoryRef.child("items").child(itemId!!).setValue(itemData).addOnSuccessListener {
                // After saving to the selected category, also save the item in the "All" category
                saveItemToAllCategory(
                    itemData,
                    categoryName,
                    categoryImageUrl
                ) // Call to save to All category
            }.addOnFailureListener {
                _uploadStatus.value = BlinkitConstants.FAILED_TO_SAVE_ITEM
            }
        }.addOnFailureListener {
            _uploadStatus.value = BlinkitConstants.FAILED_TO_SAVE_CATEGORY
        }
    }

    private fun saveItemToAllCategory(
        itemData: Map<String, String?>,
        categoryName: String,
        categoryImageUrl: String
    ) {
        val databaseReference =
            FirebaseDatabase.getInstance().reference.child("BlinkitItems").child("All")

        // Create a map for the category data
        val categoryData = mapOf(
            "name" to categoryName,
            "categoryImageUrl" to categoryImageUrl
        )

        // Set the category data for the "All" category
        databaseReference.child("categoryInfo").setValue(categoryData).addOnSuccessListener {
            // Save the item under the "All" category
            val itemId = itemData["id"] ?: databaseReference.push().key
            // Create a new map without the categoryImageUrl
            val itemDataWithoutCategoryImageUrl = itemData.toMutableMap().apply {
                // Remove any keys that should not be included in the All category
                remove("categoryImageUrl")
            }
            // Update the All category with the item data
            databaseReference.child("Items").child(itemId!!)
                .setValue(itemDataWithoutCategoryImageUrl).addOnSuccessListener {
                _uploadStatus.value = BlinkitConstants.ITEM_SAVED_SUCCESSFULLY
            }.addOnFailureListener {
                _uploadStatus.value = BlinkitConstants.FAILED_TO_SAVE_ITEM
            }
        }.addOnFailureListener {
            _uploadStatus.value = BlinkitConstants.FAILED_TO_SAVE_ALL_CATEGORY
        }
    }
}