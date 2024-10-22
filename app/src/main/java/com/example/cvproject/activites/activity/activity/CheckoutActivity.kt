package com.example.cvproject.activites.activity.activity

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.cvproject.activites.activity.adapters.CheckoutAdapter
import com.example.cvproject.activites.activity.base.BaseActivity
import com.example.cvproject.activites.activity.dataBase.BlinkitDatabase
import com.example.cvproject.activites.activity.dataclass.ItemDataClass
import com.example.cvproject.activites.activity.utilities.Utils
import com.example.cvproject.activites.activity.viewmodeles.CheckoutActivityVM
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
}