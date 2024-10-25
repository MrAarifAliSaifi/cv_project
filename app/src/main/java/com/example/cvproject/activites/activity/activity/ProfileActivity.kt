package com.example.cvproject.activites.activity.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.example.basicmvvmapp.MainActivity
import com.example.cvproject.activites.activity.base.BaseActivity
import com.example.cvproject.activites.activity.dataBase.BlinkitDatabase
import com.example.cvproject.activites.activity.utilities.Utils
import com.example.cvproject.activites.activity.viewmodeles.ProfileVM
import com.example.cvproject.activites.activity.viewmodeles.profileVMFactory
import cvproject.blinkit.databinding.ProfileActivityBinding

class ProfileActivity : BaseActivity<ProfileActivityBinding, ProfileVM>() {
    private var selectedImageUri: Uri? = null
    private val viewmodel:ProfileVM by viewModels()

    companion object {
        const val TAG = "ProfileActivity"
        fun getStartIntent(context: Context): Intent {
            return Intent(context, ProfileActivity::class.java).apply {}
        }
    }

    override fun initializeViewBinding(): ProfileActivityBinding {
        return ProfileActivityBinding.inflate(layoutInflater)
    }

    override fun initializeViewModel(): ProfileVM {
        val database = BlinkitDatabase.getDatabase(this)
        val homePageItemsDao = database.blinkitDao()
        val factory = profileVMFactory(homePageItemsDao)
        return viewModels<ProfileVM> { factory }.value
    }

    override fun setupUI() {
        val database = BlinkitDatabase.getDatabase(this)
        val homePageItemsDao = database.blinkitDao()
        Utils.setStatusBarColour(this)
    }

    override fun setupListeners() {
        binding.btnUploadImage.setOnClickListener {
            openImagePicker()
        }

        binding.btnSubmit.setOnClickListener {
            submitProfile()
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)
    }

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    selectedImageUri = uri
                    binding.ivProfileImage.setImageURI(uri) // Set the image URI to ImageView
                }
            }
        }

    private fun submitProfile() {
        val name = binding.etName.text.toString()
        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
            return
        }
        if (selectedImageUri == null) {
            Toast.makeText(this, "Please upload a profile image", Toast.LENGTH_SHORT).show()
            return
        }

        viewmodel.saveUserInfo(name, selectedImageUri.toString())
        Toast.makeText(this, "Profile submitted successfully!", Toast.LENGTH_SHORT).show()
        moveToMainActivity()
    }

    override fun observeViewModel() {
    }

   private fun moveToMainActivity(){
       val intent = MainActivity.getStartIntent(this)
       startActivity(intent)    }
}