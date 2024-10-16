package com.example.cvproject.activites.activity.activity

import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.viewModels
import com.example.cvproject.activites.activity.base.BaseActivity
import com.example.cvproject.activites.activity.viewmodeles.LoginActivityVM
import cvproject.blinkit.databinding.ActivityLoginBinding

class LoginActivity : BaseActivity<ActivityLoginBinding, LoginActivityVM>() {

    private val viewmodel: LoginActivityVM by viewModels()

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
        checkLengthOfMobileNumber()
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

    private fun checkLengthOfMobileNumber(){
        binding.etMobile.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed before the text is changed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Enable the button when the text length is 10, otherwise disable it
                binding.btnSubmit.isEnabled = s?.length == 10
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }
}