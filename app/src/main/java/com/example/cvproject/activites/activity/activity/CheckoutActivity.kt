package com.example.cvproject.activites.activity.activity

import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cvproject.activites.activity.adapters.CheckoutAdapter
import com.example.cvproject.activites.activity.base.BaseActivity
import com.example.cvproject.activites.activity.dataclass.ItemDataClass
import com.example.cvproject.activites.activity.utilities.Utils
import com.example.cvproject.activites.activity.viewmodeles.CheckoutActivityVM
import com.pixplicity.easyprefs.library.Prefs
import cvproject.blinkit.R
import cvproject.blinkit.databinding.ActivityCheckoutBinding

class CheckoutActivity : BaseActivity<ActivityCheckoutBinding, CheckoutActivityVM>() {

    private val checkoutViewModel: CheckoutActivityVM by viewModels()
    private lateinit var price: String
    private val filteredList = mutableListOf<ItemDataClass>()
    private lateinit var adapter: CheckoutAdapter
    private var itemId: String? = null

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
        binding.apply {
            header.xTitle.text = getString(R.string.checkout)
            val intent = intent
            itemId = intent.getStringExtra("itemId")
            tvDeliveeringTo.text =
                getString(R.string.delivering_to_home) + "\n" + Prefs.getString("location")
        }
        setUpRecyclerView()
        fetchData()
    }

    override fun observeViewModel() {

    }

    private fun setUpRecyclerView() {
        adapter = CheckoutAdapter(this@CheckoutActivity, filteredList)
        binding.recyclerView.adapter = adapter
        val layoutManager = LinearLayoutManager(this@CheckoutActivity)
        binding.recyclerView.layoutManager = layoutManager
    }

    private fun fetchData() {
        Utils.fetchItemDetailsById(itemId) { name, price, quantity, imageUrl ->
            if (name != null && price != null && quantity != null && imageUrl != null) {
                val item = ItemDataClass(
                    id = itemId,
                    name = name,
                    price = price,
                    quantity = quantity,
                    imageUrl = imageUrl
                )
                filteredList.add(item)
                adapter.notifyDataSetChanged()
            } else {
                Utils.showToast(this@CheckoutActivity, "Item not found or an error occurred.")
            }
        }
    }
}