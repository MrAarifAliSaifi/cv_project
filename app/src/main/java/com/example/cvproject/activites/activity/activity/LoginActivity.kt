package com.example.cvproject.activites.activity.activity

import android.content.Context
import android.content.Intent
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

    override fun setupUI() {

    }

    override fun setupListeners() {
    }

    override fun observeViewModel() {
    }
}