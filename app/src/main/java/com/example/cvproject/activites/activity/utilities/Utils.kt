package com.example.cvproject.activites.activity.utilities

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.BounceInterpolator
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.cvproject.activites.activity.dataBase.HomeItems
import com.example.cvproject.activites.activity.dataclass.ItemDataClass
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import cvproject.blinkit.R

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
        val databaseReference = FirebaseDatabase.getInstance().getReference("BlinkitItems")
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


    fun animateView(viewToAnimate: View) {
        // Translate from bottom to top
        val animation = ObjectAnimator.ofFloat(
            viewToAnimate,
            "translationY",
            viewToAnimate.height.toFloat(),
            0f
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
}