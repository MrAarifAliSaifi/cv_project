package com.example.cvproject.activites.activity.activity

import android.annotation.SuppressLint
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.basicmvvmapp.MainActivity
import com.example.cvproject.activites.activity.base.BaseActivity
import com.example.cvproject.activites.activity.viewmodeles.SplashVM
import cvproject.blinkit.databinding.SpalshBindingBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<SpalshBindingBinding, SplashVM>() {

    private val mainViewModel: SplashVM by viewModels()


    override fun initializeViewModel(): SplashVM {
        return mainViewModel
    }

    override fun initializeViewBinding(): SpalshBindingBinding {
        return SpalshBindingBinding.inflate(layoutInflater)
    }


    override fun setupUI() {
        decideNextScreen()
    }

    override fun setupListeners() {
    }

    override fun observeViewModel() {
    }

    private fun decideNextScreen() {
        lifecycleScope.launch {
            delay(2000) // 2-second delay

            if (isUserLoggedIn()) {
                val mainIntent = MainActivity.getStartIntent(this@SplashActivity)
                startActivity(mainIntent)
            } else {
                val mainIntent = LoginActivity.getStartIntent(this@SplashActivity)
                startActivity(mainIntent)
            }
            finish()
        }
    }

    private fun isUserLoggedIn(): Boolean {
        return true
    }
}