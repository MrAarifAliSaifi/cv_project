package com.example.cvproject.activites.activity.utilities

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.widget.Toast
import android.os.Build
import android.view.View
import cvproject.blinkit.R
import androidx.core.content.ContextCompat
import com.example.cvproject.activites.activity.dataclass.HomeItem
import com.example.cvproject.activites.activity.dataclass.ItemDataClass
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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


    fun fetchItemDetailsById(
        itemId: String?, callback: (String?, String?, String?, String?) -> Unit
    ) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("BlinkitItems")
        databaseReference.child(itemId!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                   if (snapshot.exists()) {
                        val item = snapshot.getValue(ItemDataClass::class.java)
                        if (item != null) {
                            callback(item.name, item.price, item.quantity, item.imageUrl)
                        } else {
                            callback(null, null, null, null)
                        }
                    } else {
                        callback(null, null, null, null)
                    }
                }
                
                override fun onCancelled(error: DatabaseError) {
                    callback(null, null, null, null)
                }
            })
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
}