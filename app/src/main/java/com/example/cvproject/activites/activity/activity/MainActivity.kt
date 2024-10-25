package com.example.basicmvvmapp

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.cvproject.activites.activity.activity.CheckoutActivity
import com.example.cvproject.activites.activity.activity.ProfileActivity
import com.example.cvproject.activites.activity.base.BaseActivity
import com.example.cvproject.activites.activity.constant.BlinkitConstants
import com.example.cvproject.activites.activity.dataBase.BlinkitDatabase
import com.example.cvproject.activites.activity.utilities.Utils
import com.example.cvproject.activites.activity.viewmodeles.MainActivityVM
import com.example.cvproject.activites.activity.viewmodeles.MainVMFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pixplicity.easyprefs.library.Prefs
import cvproject.blinkit.R
import cvproject.blinkit.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding, MainActivityVM>() {

    private val viewmodel: MainActivityVM by viewModels()

    companion object {
        const val TAG = "MainActivity"
        fun getStartIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java).apply {

            }
        }
    }

    override fun initializeViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun initializeViewModel(): MainActivityVM {
        val database = BlinkitDatabase.getDatabase(this)
        val homePageItemsDao = database.blinkitDao()
        val factory = MainVMFactory(homePageItemsDao)
        return viewModels<MainActivityVM> { factory }.value
    }

    override fun setupUI() {
        val database = BlinkitDatabase.getDatabase(this)
        viewmodel.fetchUserInfo()
        if (Utils.isInternetConnected(this@MainActivity)) {
            setupNavigation()
        } else {
            Utils.showPersistentSnackBar(
                getString(R.string.err_msg_no_internet_connection), binding.root
            )
        }
        completeProfileDalog()

    }

    override fun setupListeners() {
        binding.cartLayout.root.setOnClickListener {
            startActivity(CheckoutActivity.getStartIntent(this@MainActivity))
        }
    }

    override fun observeViewModel() {
        viewModel.userInfo.observe(this) { userInfo ->
            if (userInfo ==null) {}
        }
    }

    private fun setupNavigation() {
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_home -> {
                    showCartIcon()
                    updateCartValues()
                }

                R.id.navigation_dashboard -> {
                    showCartIcon()
                    updateCartValues()
                }

                else -> {
                    hideCartIcon()
                }
            }
        }
    }

    fun showCartIcon() {
        Utils.animateViewFromBottomToTop(binding.cartLayout.root)
        if (Prefs.getBoolean(BlinkitConstants.IS_ITEM_ADDED, false)) {
            binding.cartLayout.root.visibility = View.VISIBLE
        }
    }

    fun hideCartIcon() {
        binding.cartLayout.root.visibility = View.GONE
    }

    fun updateCartValues() {
        Glide.with(this).load(Prefs.getString(BlinkitConstants.SELECTED_ITEM_IMAGE_URL))
            .into(binding.cartLayout.imageViewItemImage)
        binding.cartLayout.tvCartItemName.text =
            Prefs.getString(BlinkitConstants.SELECTED_ITEM_DETAILS)
    }

    fun showBottomNavAndCart() {
        if (Prefs.getString(BlinkitConstants.SELECTED_ITEM_DETAILS).isNotEmpty() &&
            Prefs.getString(BlinkitConstants.SELECTED_ITEM_IMAGE_URL).isNotEmpty()
        ) {
            binding.cartLayout.root.visibility = View.VISIBLE
            Utils.animateViewFromBottomToTop(binding.cartLayout.root)
        }
        binding.navView.visibility = View.VISIBLE
        Utils.animateViewFromBottomToTop(binding.navView)
    }

    fun hideBottomNavAndCart() {
        binding.cartLayout.root.visibility = View.GONE
        binding.navView.visibility = View.GONE
    }

    private fun completeProfileDalog() {
        val builder = AlertDialog.Builder(this) // Use 'requireContext()' if inside a Fragment
        builder.setTitle("Profile")
        builder.setMessage("Please Complete Your Profile First!!")
        builder.setCancelable(false)
        builder.setPositiveButton("OK") { dialog, _ ->
            moveTpProfileScreen()
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun moveTpProfileScreen() {
        startActivity(Intent(ProfileActivity.getStartIntent(this)))
    }
}
