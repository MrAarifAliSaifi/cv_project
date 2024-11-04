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
        Utils.callPolicyFunction()
        val (title, body) = generateRandomGroceryNotification()
        scheduleRandomNotification(title, body)
    }

    private fun scheduleRandomNotification(title: String, body: String) {
        val randomDelay = Random.nextLong(30_000, 60_000) // Random delay between 30s and 60s

        CoroutineScope(Dispatchers.Main).launch {
            delay(randomDelay) // Wait for the random delay
            Utils.sendNotification(
                this@SplashActivity,
                title,
                body
            ) // Call the sendNotification function
        }
    }

    private fun generateRandomGroceryNotification(): Pair<String, String> {
        val groceryItems = listOf(
            "Apples",
            "Bananas",
            "Carrots",
            "Lettuce",
            "Tomatoes",
            "Chicken",
            "Bread",
            "Milk",
            "Eggs",
            "Cheese"
        )

        val actionMessages = listOf(
            "is now on sale!",
            "is back in stock!",
            "has a special offer!",
            "is fresh and available!",
            "is available for delivery!"
        )

        // Generate random indices
        val itemIndex = Random.nextInt(groceryItems.size)
        val messageIndex = Random.nextInt(actionMessages.size)

        // Generate random title and body
        val title = "Grocery Update: ${groceryItems[itemIndex]}"
        val body = "${groceryItems[itemIndex]} ${actionMessages[messageIndex]}"

        return Pair(title, body)
    }
}