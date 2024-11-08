package com.example.cvproject.activites.activity.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.cvproject.activites.activity.adapters.CheckoutAdapter
import com.example.cvproject.activites.activity.base.BaseActivity
import com.example.cvproject.activites.activity.constant.BlinkitConstants
import com.example.cvproject.activites.activity.dataBase.BlinkitDatabase
import com.example.cvproject.activites.activity.dataclass.ItemDataClass
import com.example.cvproject.activites.activity.utilities.Utils
import com.example.cvproject.activites.activity.viewmodeles.CheckoutActivityVM
import com.phonepe.intent.sdk.api.PhonePe
import com.phonepe.intent.sdk.api.models.PhonePeEnvironment
import com.pixplicity.easyprefs.library.Prefs
import cvproject.blinkit.R
import cvproject.blinkit.databinding.ActivityCheckoutBinding

class CheckoutActivity : BaseActivity<ActivityCheckoutBinding, CheckoutActivityVM>() {

    private val checkoutViewModel: CheckoutActivityVM by viewModels()
    private val filteredList = mutableListOf<ItemDataClass>()
    private lateinit var adapter: CheckoutAdapter
    private lateinit var blinkitDatabase: BlinkitDatabase

    companion object {
        const val TAG = "CheckoutActivity"
        fun getStartIntent(context: Context): Intent {
            return Intent(context, CheckoutActivity::class.java)
        }
    }

    override fun initializeViewBinding(): ActivityCheckoutBinding {
        return ActivityCheckoutBinding.inflate(layoutInflater)
    }

    override fun initializeViewModel(): CheckoutActivityVM {
        return checkoutViewModel
    }

    override fun setupListeners() {
        binding.apply {
            header.xBack.setOnClickListener {
                this@CheckoutActivity.finish()
            }
        }

        binding.tvPlaceOrder.setOnClickListener {
            payUsingUPI("10", "8755600408@paytm", "Recipient Name", "Payment for Order")
        }
    }

    override fun setupUI() {
        initializeBlinkitDatabase()
        binding.apply {
            header.xTitle.text = getString(R.string.checkout)
            tvDeliveeringTo.text = TextUtils.concat(
                Utils.styleStrings(getString(R.string.delivering_to_home)),
                "\n",
                Prefs.getString("location")
            )
        }
        setUpRecyclerView()
        checkoutViewModel.fetchAllSavedItemUrlsAndDetails()
    }

    override fun observeViewModel() {
        checkoutViewModel.itemUrls.observe(this) { items ->
            if (items.isNotEmpty()) {
                filteredList.clear()
                filteredList.addAll(items)
                adapter.notifyDataSetChanged()
                adapter.calculateTotalPrice(filteredList)
            } else {
                Utils.showToast(this@CheckoutActivity, "No items found")
            }
        }
    }

    private fun setUpRecyclerView() {
        adapter = CheckoutAdapter(this@CheckoutActivity, filteredList, checkoutViewModel)
        binding.recyclerView.adapter = adapter
        val layoutManager = LinearLayoutManager(this@CheckoutActivity)
        binding.recyclerView.layoutManager = layoutManager
    }

    private fun initializeBlinkitDatabase() {
        blinkitDatabase =
            Room.databaseBuilder(applicationContext, BlinkitDatabase::class.java, "blinkitDatabase")
                .build()
    }


    // Function to create and launch the UPI payment intent
    private fun payUsingUPI(amount: String, upiId: String, name: String, note: String) {
        val uri = Uri.parse("upi://pay").buildUpon()
            .appendQueryParameter("pa", upiId) // UPI ID of the recipient
            .appendQueryParameter("pn", name) // Name of the recipient
            .appendQueryParameter("mc", "")
            .appendQueryParameter("tid", "123456789") // Unique transaction ID (can be customized)
            .appendQueryParameter("tr", "transactionRefId") // Reference ID for the transaction
            .appendQueryParameter("tn", note) // Note for the payment
            .appendQueryParameter("am", amount) // Amount in INR
            .appendQueryParameter("cu", "INR") // Currency
            .appendQueryParameter("url", "your-redirect-url") // Optional
            .build()

        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = uri
        }

        // Check if a UPI app is available to handle the payment
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, 1) // Launch the UPI payment intent
        } else {
            // Show a message if no UPI app is found
            Toast.makeText(this, "PhonePe or UPI app not installed.", Toast.LENGTH_SHORT).show()
        }
    }

    // Handle the result of the UPI payment
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {
            // Check if there's a response from the UPI app
            if (data != null) {
                val response = data.getStringExtra("response")
                when {
                    response == null -> Toast.makeText(this, "Transaction cancelled.", Toast.LENGTH_SHORT).show()
                    response.contains("SUCCESS", true) -> Toast.makeText(this, "Payment successful.", Toast.LENGTH_SHORT).show()
                    else -> Toast.makeText(this, "Payment failed: $response", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Handle case where the user cancels the transaction
                Toast.makeText(this, "Transaction cancelled.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}