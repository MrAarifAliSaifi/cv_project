package com.example.cvproject.activites.activity.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.example.cvproject.activites.activity.base.BaseActivity
import com.example.cvproject.activites.activity.utilities.Utils
import com.example.cvproject.activites.activity.viewmodeles.AdminActivityVM
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import cvproject.blinkit.R
import cvproject.blinkit.databinding.ActivityAdminBinding
import java.util.UUID

class AdminActivity : BaseActivity<ActivityAdminBinding, AdminActivityVM>() {

    private val adminViewModel: AdminActivityVM by viewModels()
    private var selectedImageUri: Uri? = null

    companion object {
        const val TAG = "AdminActivity"
        fun getStartIntent(context: Context): Intent {
            return Intent(context, AdminActivity::class.java)
        }
    }

    override fun initializeViewBinding(): ActivityAdminBinding {
        return ActivityAdminBinding.inflate(layoutInflater)
    }

    override fun initializeViewModel(): AdminActivityVM {
        return adminViewModel
    }

    override fun setupUI() {

    }

    override fun setupListeners() {
        binding.apply {
            xHeader.xTitle.text = getString(R.string.admin)
            buttonAddImage.setOnClickListener {
                openGallery()
            }
            buttonSaveImage.setOnClickListener {
                if (validateFields()) {
                    uploadItemToFirebase()
                }
            }
            xHeader.xBack.setOnClickListener {
                this@AdminActivity.finish()
            }
        }
    }

    override fun observeViewModel() {
        // Observe any LiveData from the ViewModel here if needed
    }

    private fun validateFields(): Boolean {
        binding.apply {
            if (editTextItemName.text.toString().isEmpty()) {
                Utils.showSnackBar(getString(R.string.item_name_can_t_be_empty), binding.root)
                return false
            } else if (editTextItemPrice.text.toString().isEmpty()) {
                Utils.showSnackBar(getString(R.string.item_price_can_t_be_empty), binding.root)
                return false
            } else if (selectedImageUri == null) {
                Utils.showSnackBar(
                    getString(R.string.select_image_first_to_proceed_further), binding.root
                )
                return false
            }
        }
        return true
    }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { pickedUri ->
            if (pickedUri != null) {
                this.selectedImageUri = pickedUri
                binding.apply {
                    Glide.with(this@AdminActivity).load(selectedImageUri).into(imageView)
                }
            }
        }

    private fun openGallery() {
        pickImageLauncher.launch("image/*")
    }

    private fun uploadItemToFirebase() {
        showProgress()
        val itemName = binding.editTextItemName.text.toString().trim()
        val itemPrice = binding.editTextItemPrice.text.toString().trim()

        val storageReference = FirebaseStorage.getInstance().reference
        val imageRef = storageReference.child("Images/${UUID.randomUUID()}.jpg")
        imageRef.putFile(selectedImageUri!!).addOnSuccessListener { taskSnapshot ->
            imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                // Get the download URL and save the item data
                saveItemDataToDatabase(itemName, itemPrice, downloadUri.toString())
            }
        }.addOnFailureListener {
            hideProgress()
            Utils.showToast(this, getString(R.string.failed_to_save_item))
        }
    }

    private fun saveItemDataToDatabase(name: String, price: String, imageUrl: String) {
        val databaseReference = FirebaseDatabase.getInstance().reference.child("BlinkitItems")
        val itemId = databaseReference.push().key

        val itemData = mapOf(
            "id" to itemId, "name" to name, "price" to price, "imageUrl" to imageUrl
        )

        // Save the item data to Firebase Realtime Database
        databaseReference.child(itemId!!).setValue(itemData).addOnSuccessListener {
            Utils.showToast(this, getString(R.string.item_saved_successfully))
            this@AdminActivity.finish()
            hideProgress()
        }.addOnFailureListener {
            hideProgress()
            Utils.showToast(this, getString(R.string.failed_to_save_item))
        }
    }

    private fun showProgress() {
        binding.apply {
            buttonAddImage.isEnabled = false
            progressBar.visibility = View.VISIBLE
            buttonSaveImage.visibility = View.INVISIBLE
        }
    }

    private fun hideProgress() {
        binding.apply {
            buttonAddImage.isEnabled = true
            progressBar.visibility = View.GONE
            buttonSaveImage.visibility = View.VISIBLE
        }
    }
}