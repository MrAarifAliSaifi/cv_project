package com.example.cvproject.activites.activity.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.StrictMode
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.cvproject.activites.activity.base.BaseActivity
import com.example.cvproject.activites.activity.constant.BlinkitConstants
import com.example.cvproject.activites.activity.utilities.Utils
import com.example.cvproject.activites.activity.viewmodeles.AdminActivityVM
import com.google.firebase.messaging.FirebaseMessaging
import cvproject.blinkit.R
import cvproject.blinkit.databinding.ActivityAdminBinding

class AdminActivity : BaseActivity<ActivityAdminBinding, AdminActivityVM>() {

    private val adminViewModel: AdminActivityVM by viewModels()
    private var selectedImageUri: Uri? = null
    private var categoryImageUri: Uri? = null
    private var categoryName: String? = "All"

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
        initializeSpinner()
    }

    override fun setupListeners() {
        binding.apply {
            xHeader.xTitle.text = getString(R.string.admin)
            buttonAddImage.setOnClickListener {
                openGalleryForItemImage()
            }
            buttonAddImageCategory.setOnClickListener {
                openGalleryForCategoryImage()
            }
            buttonSaveImage.setOnClickListener {
                if (validateFields()) {
                    val itemName = binding.editTextItemName.text.toString().trim()
                    val itemPrice = binding.editTextItemPrice.text.toString().trim()
                    val itemQuantity = binding.editTextItemQuantity.text.toString().trim()
                    val selectedImageUri = selectedImageUri
                    val categoryImageUri = categoryImageUri
                    val categoryName = categoryName

                    viewModel.uploadItemToFirebase(
                        itemName,
                        itemPrice,
                        itemQuantity,
                        selectedImageUri,
                        categoryName!!,
                        categoryImageUri
                    )
                }
            }
            xHeader.xBack.setOnClickListener {
                this@AdminActivity.finish()
            }
        }
    }

    override fun observeViewModel() {
        viewModel.uploadStatus.observe(this, Observer { status ->
            when (status) {
                BlinkitConstants.UPLOADING -> showProgress()
                BlinkitConstants.ITEM_SAVED_SUCCESSFULLY -> {
                    hideProgress()
                    FirebaseMessaging.getInstance().subscribeToTopic("topic")
                    // Below line is important to generate bearer token
                    Utils.callPolicyFunction()
                    Utils.showToast(this, "Item saved successfully")
                    Utils.sendNotification(
                        this@AdminActivity,
                        binding.editTextItemName.text.toString().trim(),
                        binding.editTextItemPrice.text.toString().trim()
                    )
                    finish()
                }

                BlinkitConstants.FAILED_TO_UPLOAD_IMAGE -> {
                    hideProgress()
                    Utils.showToast(this, "Failed to upload image")
                }

                BlinkitConstants.FAILED_TO_SAVE_ITEM -> {
                    hideProgress()
                    Utils.showToast(this, "Failed to save item")
                }

                BlinkitConstants.NO_IMAGE_SELECTED -> {
                    hideProgress()
                    Utils.showToast(this, "Please select an image")
                }
            }
        })
    }

    private fun validateFields(): Boolean {
        binding.apply {
            if (editTextItemName.text.toString().isEmpty()) {
                Utils.showSnackBar(getString(R.string.item_name_can_t_be_empty), binding.root)
                return false
            } else if (editTextItemPrice.text.toString().isEmpty()) {
                Utils.showSnackBar(getString(R.string.item_price_can_t_be_empty), binding.root)
                return false
            } else if (editTextItemQuantity.text.toString().isEmpty()) {
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

    private val pickCategoryImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { pickedUri ->
            if (pickedUri != null) {
                this.categoryImageUri = pickedUri
                binding.apply {
                    Glide.with(this@AdminActivity).load(categoryImageUri).into(imageViewCategory)
                }
            }
        }

    private fun openGalleryForItemImage() {
        pickImageLauncher.launch("image/*")
    }

    private fun openGalleryForCategoryImage() {
        pickCategoryImageLauncher.launch("image/*")
    }

    private fun showProgress() {
        binding.apply {
            buttonAddImage.visibility = View.INVISIBLE
            buttonAddImageCategory.visibility = View.INVISIBLE
            progressBar.visibility = View.VISIBLE
            buttonSaveImage.visibility = View.INVISIBLE
        }
    }

    private fun hideProgress() {
        binding.apply {
            buttonAddImage.visibility = View.VISIBLE
            buttonAddImageCategory.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
            buttonSaveImage.visibility = View.VISIBLE
        }
    }

    private fun initializeSpinner() {
        val categories = listOf(
            "All",
            "Soap and Beauty",
            "Beverages",
            "Cereals",
            "Fruits & Vegetables",
            "Dairy & Eggs",
            "Snacks & Confectionery",
            "Household Essentials"
        )
        // Adapter for Spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = adapter

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                val selectedCategory = categories[position]
                if (selectedCategory != "All") {
                    categoryName = selectedCategory
                    binding.clRight.visibility = View.VISIBLE
                } else {
                    binding.clRight.visibility = View.GONE
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }
}