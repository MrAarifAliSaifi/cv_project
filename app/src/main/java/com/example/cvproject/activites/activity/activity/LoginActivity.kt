package com.example.cvproject.activites.activity.activity

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.basicmvvmapp.MainActivity
import com.example.cvproject.activites.activity.adapters.ViewPagerAdapter
import com.example.cvproject.activites.activity.base.BaseActivity
import com.example.cvproject.activites.activity.constant.BlinkitConstants
import com.example.cvproject.activites.activity.dataclass.OnboardingItem
import com.example.cvproject.activites.activity.utilities.Utils
import com.example.cvproject.activites.activity.utilities.Utils.gone
import com.example.cvproject.activites.activity.utilities.Utils.visible
import com.example.cvproject.activites.activity.viewmodeles.LoginActivityVM
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.pixplicity.easyprefs.library.Prefs
import cvproject.blinkit.databinding.ActivityLoginBinding
import java.util.concurrent.TimeUnit

class LoginActivity : BaseActivity<ActivityLoginBinding, LoginActivityVM>() {
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var auth: FirebaseAuth
    private var verificationId: String? = null
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

    override fun setupUI() {
        auth = FirebaseAuth.getInstance()
        checkLength()
        Utils.setStatusBarColour(this)
        setupViewPager(viewmodel.getOnboardingCarouselData(this))
    }

    override fun setupListeners() {
        binding.btnSubmit.setOnClickListener {
            val mobileNumber = binding.etMobile.text.toString().trim()
            if (mobileNumber.isEmpty()) {
                Utils.showToast(this@LoginActivity, "Please enter a valid mobile number")
            } else {
                sendVerificationCode("+91" + mobileNumber)
            }
        }

    }

    override fun observeViewModel() {

    }

    private fun checkLength() {
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
            onboardingCarouselViewPagerAdapter = ViewPagerAdapter(this@LoginActivity, carouselData)
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

    private fun sendVerificationCode(phoneNumber: String) {
        binding.flProgressCircular.visible()
        binding.etMobile.isEnabled=false
        binding.btnSubmit.isEnabled=false
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signInWithCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Utils.showToast(this@LoginActivity, "Verification Failed")
                    binding.flProgressCircular.gone()
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    this@LoginActivity.verificationId = verificationId
                    Utils.showToast(this@LoginActivity, "Otp sent Successfully")
                    binding.flProgressCircular.gone()
                    binding.etMobile.isEnabled=true

                    val intent =
                        OTPActivity.getStartIntent(this@LoginActivity, phoneNumber, verificationId)
                    startActivity(intent)
                    finish()
                }
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Utils.showToast(this, "Login successful")
                    binding.flProgressCircular.gone()
                    binding.etMobile.isEnabled=true
                    val intent = MainActivity.getStartIntent(this)
                    Prefs.putString(BlinkitConstants.Verification_ID, verificationId)
                    Prefs.putBoolean(BlinkitConstants.IS_LOGGED_IN,true)
                    startActivity(intent)
                    finish()
                } else {
                    binding.flProgressCircular.gone()
                    binding.etMobile.isEnabled=true
                    Utils.showToast(this, "Login Failed")
                }
            }
    }
}