package com.example.cvproject.activites.activity.activity

import android.annotation.SuppressLint
import androidx.activity.viewModels
import com.example.basicmvvmapp.MainActivity
import com.example.cvproject.activites.activity.base.BaseActivity
import com.example.cvproject.activites.activity.constant.BlinkitConstants
import com.example.cvproject.activites.activity.utilities.Utils
import com.example.cvproject.activites.activity.viewmodeles.SplashVM
import com.google.firebase.FirebaseApp
import com.pixplicity.easyprefs.library.Prefs
import cvproject.blinkit.databinding.SpalshBindingBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

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
        Utils.setStatusBarColour(this)
        decideNextScreen()
    }

    override fun setupListeners() {
    }

    override fun observeViewModel() {
    }

    private fun decideNextScreen() {
        val isLoggedIn = Prefs.getBoolean(BlinkitConstants.IS_LOGGED_IN, true)
        if (isLoggedIn) {
            val intent = MainActivity.getStartIntent(this)
            startActivity(intent)
        } else {
            val intent = LoginActivity.getStartIntent(this)
            startActivity(intent)
        }
        finish()
    }
}