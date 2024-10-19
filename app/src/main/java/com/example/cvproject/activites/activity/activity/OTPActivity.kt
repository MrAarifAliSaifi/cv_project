package com.example.cvproject.activites.activity.activity

import android.content.Context
import android.content.Intent
import android.view.KeyEvent
import android.widget.Toast
import androidx.activity.viewModels
import com.example.basicmvvmapp.MainActivity
import com.example.cvproject.activites.activity.base.BaseActivity
import com.example.cvproject.activites.activity.constant.BlinkitConstants
import com.example.cvproject.activites.activity.textWatcher.TextWatcherWrapper
import com.example.cvproject.activites.activity.viewmodeles.OtpActivityVM
import com.pixplicity.easyprefs.library.Prefs
import cvproject.blinkit.R
import cvproject.blinkit.databinding.OtpActivityBinding

class OTPActivity : BaseActivity<OtpActivityBinding, OtpActivityVM>() {

    private val viewmodel: OtpActivityVM by viewModels()

    companion object {
        const val TAG = "OTPActivity"
        private const val EXTRA_MOBILE_NUMBER = "mobile_number"

        fun getStartIntent(context: Context, mobileNumber: String): Intent {
            return Intent(context, OTPActivity::class.java).apply {
                putExtra(EXTRA_MOBILE_NUMBER, mobileNumber)
            }
        }
    }

    override fun initializeViewBinding(): OtpActivityBinding {
        return OtpActivityBinding.inflate(layoutInflater)
    }

    override fun initializeViewModel(): OtpActivityVM {
        return viewmodel
    }

    override fun setupUI() {
        val mobileNumber = intent.getStringExtra(EXTRA_MOBILE_NUMBER)
        binding.tvUserContact.text = mobileNumber
    }

    override fun setupListeners() {
        binding.etOne.addTextChangedListener(object : TextWatcherWrapper {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val char = p0 ?: ""
                if (char.length == 6) {
                    pasteOtpInAllBoxes(char.toString())
                } else if (char.length == 1 && binding.etTwo.text?.isEmpty() == true) {
                    binding.etTwo.requestFocus()
                } else if (char.length in 2..5 || char.length > 6) {
                    toastS(getString(R.string.msg_six_word_otp))
                } else if (binding.etOne.text?.isNotEmpty() == true && binding.etTwo.text?.isNotEmpty() == true && binding.etThree.text?.isNotEmpty() == true && binding.etFour.text?.isNotEmpty() == true && binding.etFive.text?.isNotEmpty() == true && binding.etSix.text?.isNotEmpty() == true) {
                    showVerifyButton()
                } else {
                    hideVerifyButton()
                }
            }
        })

        binding.etOne.setOnClickListener {
            binding.etOne.setSelection(binding.etOne.text.toString().length)
        }

        binding.etTwo.addTextChangedListener(object : TextWatcherWrapper {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val char = p0 ?: ""
                if (char.length == 6) {
                    pasteOtpInAllBoxes(char.toString())
                } else if (char.isEmpty()) {
                    binding.etOne.requestFocus()
                } else if (char.length == 1 && binding.etThree.text?.isEmpty() == true) {
                    binding.etThree.requestFocus()
                } else if (char.length in 2..5 || char.length > 6) {
                    toastS(getString(R.string.msg_six_word_otp))
                } else if (binding.etOne.text?.isNotEmpty() == true && binding.etTwo.text?.isNotEmpty() == true && binding.etThree.text?.isNotEmpty() == true && binding.etFour.text?.isNotEmpty() == true && binding.etFive.text?.isNotEmpty() == true && binding.etSix.text?.isNotEmpty() == true) {
                    showVerifyButton()
                } else {
                    hideVerifyButton()
                }
            }
        })

        binding.etTwo.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN && binding.etTwo.text?.trim()
                    .toString().isEmpty()
            ) {
                binding.etOne.requestFocus()
            }
            false
        }

        binding.etTwo.setOnClickListener {
            binding.etTwo.setSelection(binding.etTwo.text.toString().length)
        }

        binding.etThree.addTextChangedListener(object : TextWatcherWrapper {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val char = p0 ?: ""
                if (char.length == 6) {
                    pasteOtpInAllBoxes(char.toString())
                } else if (char.isEmpty()) {
                    binding.etTwo.requestFocus()
                } else if (char.length == 1 && binding.etFour.text?.isEmpty() == true) {
                    binding.etFour.requestFocus()
                } else if (char.length in 2..5 || char.length > 6) {
                    toastS(getString(R.string.msg_six_word_otp))
                } else if (binding.etOne.text?.isNotEmpty() == true && binding.etTwo.text?.isNotEmpty() == true && binding.etThree.text?.isNotEmpty() == true && binding.etFour.text?.isNotEmpty() == true && binding.etFive.text?.isNotEmpty() == true && binding.etSix.text?.isNotEmpty() == true) {
                    showVerifyButton()
                } else {
                    hideVerifyButton()
                }
            }
        })

        binding.etThree.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN && binding.etThree.text?.trim()
                    .toString().isEmpty()
            ) {
                binding.etTwo.requestFocus()
            }
            false
        }

        binding.etThree.setOnClickListener {
            binding.etThree.setSelection(binding.etThree.text.toString().length)
        }

        binding.etFour.addTextChangedListener(object : TextWatcherWrapper {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val char = p0 ?: ""
                if (char.length == 6) {
                    pasteOtpInAllBoxes(char.toString())
                } else if (char.isEmpty()) {
                    binding.etThree.requestFocus()
                } else if (char.length == 1 && binding.etFive.text?.isEmpty() == true) {
                    binding.etFive.requestFocus()
                } else if (char.length in 2..5 || char.length > 6) {
                    toastS(getString(R.string.msg_six_word_otp))
                } else if (binding.etOne.text?.isNotEmpty() == true && binding.etTwo.text?.isNotEmpty() == true && binding.etThree.text?.isNotEmpty() == true && binding.etFour.text?.isNotEmpty() == true && binding.etFive.text?.isNotEmpty() == true && binding.etSix.text?.isNotEmpty() == true) {
                    showVerifyButton()
                } else {
                    hideVerifyButton()
                }
            }
        })

        binding.etFour.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN && binding.etFour.text?.trim()
                    .toString().isEmpty()
            ) {
                binding.etThree.requestFocus()
            }
            false
        }

        binding.etFour.setOnClickListener {
            binding.etFour.setSelection(binding.etFour.text.toString().length)
        }

        binding.etFive.addTextChangedListener(object : TextWatcherWrapper {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val char = p0 ?: ""
                if (char.length == 6) {
                    pasteOtpInAllBoxes(char.toString())
                } else if (char.isEmpty()) {
                    binding.etFour.requestFocus()
                } else if (char.length == 1 && binding.etSix.text?.isEmpty() == true) {
                    binding.etSix.requestFocus()
                } else if (char.length in 2..5 || char.length > 6) {
                    toastS(getString(R.string.msg_six_word_otp))
                } else if (binding.etOne.text?.isNotEmpty() == true && binding.etTwo.text?.isNotEmpty() == true && binding.etThree.text?.isNotEmpty() == true && binding.etFour.text?.isNotEmpty() == true && binding.etFive.text?.isNotEmpty() == true && binding.etSix.text?.isNotEmpty() == true) {
                    showVerifyButton()
                } else {
                    hideVerifyButton()
                }
            }
        })

        binding.etFive.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN && binding.etFive.text?.trim()
                    .toString().isEmpty()
            ) {
                binding.etFour.requestFocus()
            }
            false
        }

        binding.etFive.setOnClickListener {
            binding.etFive.setSelection(binding.etFive.text.toString().length)
        }

        binding.etSix.addTextChangedListener(object : TextWatcherWrapper {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val char = p0 ?: ""
                if (char.length == 6) {
                    pasteOtpInAllBoxes(char.toString())
                } else if (char.isEmpty()) {
                    binding.etFive.requestFocus()
                } else if (char.length == 1 && binding.etSix.text?.isEmpty() == true) {
                    binding.etSix.requestFocus()
                } else if (char.length in 2..5 || char.length > 6) {
                    toastS(getString(R.string.msg_six_word_otp))
                } else if (binding.etOne.text?.isNotEmpty() == true && binding.etTwo.text?.isNotEmpty() == true && binding.etThree.text?.isNotEmpty() == true && binding.etFour.text?.isNotEmpty() == true && binding.etFive.text?.isNotEmpty() == true && binding.etSix.text?.isNotEmpty() == true) {
                    showVerifyButton()
                } else {
                    hideVerifyButton()
                }
            }
        })

        binding.etSix.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN && binding.etSix.text?.trim()
                    .toString().isEmpty()
            ) {
                binding.etFive.requestFocus()
            }
            false
        }

        binding.etSix.setOnClickListener {
            binding.etSix.setSelection(binding.etSix.text.toString().length)
        }

        binding.btnSubmit.setOnClickListener {
            if (binding.etOne.text.isNullOrBlank() || binding.etTwo.text.isNullOrBlank() || binding.etThree.text.isNullOrBlank() || binding.etFour.text.isNullOrBlank() || binding.etFive.text.isNullOrBlank() || binding.etSix.text.isNullOrBlank()) {
                toastS(getString(R.string.msg_no_otp_provided))
            } else {
                val mainIntent = MainActivity.getStartIntent(this)
                Prefs.putBoolean(BlinkitConstants.IS_LOGGED_IN, true)
                startActivity(mainIntent)
            }
        }

    }

    override fun observeViewModel() {
    }

    private fun pasteOtpInAllBoxes(otp: String) {
        binding.apply {
            etOne.setText(otp[0].toString())
            etTwo.setText(otp[1].toString())
            etThree.setText(otp[2].toString())
            etFour.setText(otp[3].toString())
            etFive.setText(otp[4].toString())
            etSix.setText(otp[5].toString())
        }
    }

    private fun showVerifyButton() {
        binding.apply {
            btnSubmit.isEnabled = true
        }
    }

    private fun hideVerifyButton() {
        binding.apply {
            btnSubmit.isEnabled = false
        }
    }

    fun Context.toastS(message: String?) {
        if (!message.isNullOrEmpty()) Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


}