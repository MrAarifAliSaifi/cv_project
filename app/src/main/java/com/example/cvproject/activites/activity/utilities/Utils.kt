package com.example.cvproject.activites.activity.utilities

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.StrictMode
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.cvproject.activites.activity.dataBase.HomeItems
import com.example.cvproject.activites.activity.dataclass.AccessToken
import com.example.cvproject.activites.activity.dataclass.ItemDataClass
import com.example.cvproject.activites.activity.dataclass.Notification
import com.example.cvproject.activites.activity.dataclass.NotificationData
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import cvproject.blinkit.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.random.Random

object Utils {

    private var snackbar: Snackbar? = null

    @JvmStatic
    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    @JvmStatic
    fun showSnackBar(message: String, view: View) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
    }

    @JvmStatic
    fun showPersistentSnackBar(message: String, view: View) {
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE)
        snackbar.setAction("Dismiss") {
            snackbar.dismiss()
        }
        snackbar.show()
    }

    fun setStatusBarColour(
        activity: Activity,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.window.statusBarColor = ContextCompat.getColor(activity, R.color.bright_yellow)
        }
    }


    fun fetchItemDetailsByUrls(
        itemUrls: List<HomeItems>, callback: (List<ItemDataClass>) -> Unit
    ) {
        val databaseReference =
            FirebaseDatabase.getInstance().getReference("BlinkitItems").child("All").child("Items")
        val itemList = mutableListOf<ItemDataClass>()
        var itemsProcessed = 0

        if (itemUrls.isEmpty()) {
            callback(itemList)
            return
        }

        for (item in itemUrls) {
            val itemUrl = item.itemIdGeneratedFromFirebase ?: continue
            databaseReference.child(itemUrl)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val item = snapshot.getValue(ItemDataClass::class.java)
                            if (item != null) {
                                itemList.add(item)
                            }
                        }
                        itemsProcessed++
                        if (itemsProcessed == itemUrls.size) {
                            callback(itemList)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        itemsProcessed++
                        if (itemsProcessed == itemUrls.size) {
                            callback(itemList)
                        }
                    }
                })
        }
    }

    fun isInternetConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val networkCapabilities =
                connectivityManager.getNetworkCapabilities(network) ?: return false
            return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            return networkInfo.isConnected
        }
    }

    fun styleStrings(firstString: String): SpannableString {
        val combinedString = "$firstString"
        val spannableString = SpannableString(combinedString)
        spannableString.setSpan(
            StyleSpan(Typeface.BOLD), 0, firstString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannableString
    }


    fun animateViewFromBottomToTop(viewToAnimate: View) {
        // Translate from bottom to top
        val animation = ObjectAnimator.ofFloat(
            viewToAnimate, "translationY", viewToAnimate.height.toFloat(), 0f
        )
        // Set animation duration in milliseconds
        animation.duration = 1000
        animation.interpolator = AccelerateDecelerateInterpolator()
        animation.start()
    }

    fun View.visible() {
        this.visibility = View.VISIBLE
    }

    fun View.gone() {
        this.visibility = View.GONE
    }

    fun returnPercentage(discountedPrice: Int, actualPrice: Int): Int {
        val diff = actualPrice - discountedPrice
        return (diff * 100) / actualPrice
    }


    fun sendNotification(title: String, body: String) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val notification = Notification(
                            message = NotificationData(
                                task.result, hashMapOf(
                                    "title" to title,
                                    "body" to body
                                )
                            )
                        )
                        Log.e("Device Token", task.result)
                        Log.e("Access Token", AccessToken.getAccessToken().toString())
                        NotificationAPI.sendNotification().notification(notification)
                            .enqueue(object : Callback<Notification> {
                                override fun onResponse(
                                    call: Call<Notification>, response: Response<Notification>
                                ) {
                                    if (response.isSuccessful) {
                                        Log.e(
                                            "TAG",
                                            "Success ${response.message()}"
                                        )
                                    } else {
                                        Log.e(
                                            "TAG",
                                            response.body().toString()
                                        )
                                    }
                                }

                                override fun onFailure(call: Call<Notification>, t: Throwable) {
                                    Log.e("TAG", t.message.toString())
                                }

                            })
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Log.e("TAG", e.message.toString())
                        }
                    }
                }
            } else {
                return@addOnCompleteListener
            }
        }
    }

    fun callPolicyFunction() {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
    }

    fun generateRandomGroceryNotification(itemNameList: MutableList<String>): Pair<String, String> {
        Log.e("itemNameList", itemNameList.toString())
        val emojis = listOf(
            "🎊", "🕒", "😍", "😂", "😅", "😭",
            "😜", "😝", "😛", "🤑", "🔥", "💯",
            "❤️", "👍", "👎", "👏", "✨", "🌟",
            "🎉", "😎", "🥳", "🤔", "🙌", "😇",
            "🤩", "🥺", "🤗", "😏", "👀", "🌈",
            "🎈", "🦄", "🍕", "🍔", "🍣", "🍰",
            "🌍", "💪", "🧡", "🎁", "🌹", "🐶"
        )

        val actionMessages = listOf(
            "🔥 is now on sale! ",
            "✅ is back in stock! ",
            "💸 has a special offer!",
            "🌿 is fresh and available!",
            "🚚 is available for delivery!",
            "⚠️ is in limited quantity!",
            "⏳ has a discount for a limited time!",
            "🍲 is perfect for today's recipe!",
            "🍁 is a must-have for this season!",
            "🆕 is freshly restocked!",
            "🎉 comes with a buy-one-get-one-free offer!",
            "💰 is available at a special price today!",
            "👨‍🍳 is recommended by chefs!",
            "⚡ is running out fast!",
            "📍 is available for pickup nearby!"
        )

        // Generate random indices
        val itemIndex = Random.nextInt(itemNameList.size)
        val messageIndex = Random.nextInt(actionMessages.size)
        // Generate a random number of emojis to add (e.g., between 1 and 5)
        val numberOfEmojis = Random.nextInt(1, 6) // Change the upper limit to allow more emojis
        // Generate a random title with multiple emojis
        val emojiList = List(numberOfEmojis) { emojis.random() } // Get a list of random emojis
        val title =
            "${itemNameList[itemIndex]} ${emojiList.joinToString("")}" // Join emojis into a string
        // Generate random body message
        val body = "${itemNameList[itemIndex]} ${actionMessages[messageIndex]}"
        return Pair(title, body)
    }

    fun scheduleRandomNotification(title: String, body: String) {
        CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                sendNotification(title, body)
                // Random delay between
                val delayTime = Random.nextLong(30000L, 60000L)
                delay(delayTime)
            }
        }
    }
}