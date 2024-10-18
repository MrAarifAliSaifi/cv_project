package com.example.cvproject.activites.activity.activity

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.cvproject.activites.activity.adapters.ViewPagerAdapter
import com.example.cvproject.activites.activity.base.BaseActivity
import com.example.cvproject.activites.activity.dataclass.OnboardingItem
import com.example.cvproject.activites.activity.utilities.Utils
import com.example.cvproject.activites.activity.viewmodeles.LoginActivityVM
import com.google.android.material.tabs.TabLayoutMediator
import cvproject.blinkit.databinding.ActivityLoginBinding

class LoginActivity : BaseActivity<ActivityLoginBinding, LoginActivityVM>() {
    private val handler = Handler(Looper.getMainLooper())
    private val viewmodel: LoginActivityVM by viewModels()
    private lateinit var onboardingCarouselViewPagerAdapter: ViewPagerAdapter
    companion object {
        const val TAG = "LoginActivity"
        fun getStartIntent(context: Context): Intent {
            return Intent(context, LoginActivity::class.java).apply {

            }
        }
    }

    override fun initializeViewBinding(): ActivityLoginBinding {
        return ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun initializeViewModel(): LoginActivityVM {
        return viewmodel
    }

    override fun setupUI(){
        checkLength()
        Utils.setStatusBarColour(this)
        setupViewPager(viewmodel.getOnboardingCarouselData(this))
    }

    override fun setupListeners(){
        binding.btnSubmit.setOnClickListener {
            val mobileNumber = binding.etMobile.text.toString().trim()
            if (mobileNumber.isEmpty()) {
                Toast.makeText(this, "Please enter a valid mobile number", Toast.LENGTH_SHORT).show()
            } else {
                val intent = OTPActivity.getStartIntent(this, mobileNumber)
                startActivity(intent)
            }
        }

    }

    override fun observeViewModel(){

    }
    private fun checkLength(){
        binding.etMobile.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.btnSubmit.isEnabled = s?.length == 10
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

        private fun setupViewPager(carouselData: List<OnboardingItem>) {
            binding.apply {
                onboardingCarouselViewPagerAdapter = ViewPagerAdapter(this@LoginActivity,carouselData)
                binding.viewPager.adapter = onboardingCarouselViewPagerAdapter
                TabLayoutMediator(tabLayout, viewPager) { _, _ -> }.attach()
                binding.viewPager.autoScroll(3000, handler)
                binding.viewPager.offscreenPageLimit = 1
                binding.tabLayout.visibility = View.VISIBLE
                binding.viewPager.visibility = View.VISIBLE
            }
        }

    fun ViewPager2.autoScroll(interval: Long, handler: Handler) {
        var scrollPosition = 0

        val runnable = object : Runnable {

            override fun run() {
                val count = adapter?.itemCount ?: 0
                setCurrentItem(scrollPosition++ % count, true)

                handler.postDelayed(this, interval)
            }
        }
        registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }
        })

        handler.post(runnable)
    }
}