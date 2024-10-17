package com.example.basicmvvmapp
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.viewModels
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.cvproject.activites.activity.base.BaseActivity
import com.example.cvproject.activites.activity.viewmodeles.MainActivityVM
import com.google.android.material.bottomnavigation.BottomNavigationView
import cvproject.blinkit.R
import cvproject.blinkit.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding, MainActivityVM>() {
    private val viewmodel: MainActivityVM by viewModels()
    private var doubleBackToExitPressedOnce = false

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
        return viewmodel
    }

    override fun setupUI() {
        setupNavigation()
    }

    override fun setupListeners() {
    }

    override fun observeViewModel() {
    }

    private fun setupNavigation() {
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)
    }

    override fun onBackPressed() {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        if (navController.currentDestination?.id == navController.graph.startDestinationId) {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed()
                return
            }
            doubleBackToExitPressedOnce = true
            Toast.makeText(this, "Click again to exit", Toast.LENGTH_SHORT).show()
            Handler(Looper.getMainLooper()).postDelayed({
                doubleBackToExitPressedOnce = false
            }, 2000)
        } else {
            super.onBackPressed()
        }
    }
}
