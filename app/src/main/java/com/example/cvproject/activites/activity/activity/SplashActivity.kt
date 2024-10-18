package com.example.cvproject.activites.activity.activity

import android.annotation.SuppressLint
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.basicmvvmapp.MainActivity
import com.example.cvproject.activites.activity.base.BaseActivity
import com.example.cvproject.activites.activity.viewmodeles.SplashVM
import com.google.firebase.BuildConfig
import com.google.firebase.FirebaseApp
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
        FirebaseApp.initializeApp(this)
//         val currentUser = FirebaseAuth.getInstance().currentUser
//        if (currentUser != null) {
//            Toast.makeText(this, "register", Toast.LENGTH_SHORT).show()
//        }else {
//            Toast.makeText(this, " not register", Toast.LENGTH_SHORT).show()
//        }
        decideNextScreen()
    }

    override fun setupListeners() {
    }

    override fun observeViewModel() {
    }

    private fun decideNextScreen() {
        if (!BuildConfig.DEBUG) {
            startActivity(MainActivity.getStartIntent(this@SplashActivity))
        } else {
            lifecycleScope.launch {
                delay(2000)

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
    }

    private fun isUserLoggedIn(): Boolean {
        return false
    }
}