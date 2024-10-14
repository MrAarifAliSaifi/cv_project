package com.example.cvproject.activites.activity.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding


abstract class BaseActivity<Binding : ViewBinding, VM : ViewModel> : AppCompatActivity() {

    protected lateinit var binding: Binding
    protected lateinit var viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = initializeViewBinding() // Initialize ViewBinding
        viewModel = initializeViewModel() // Initialize ViewModel

        setContentView(binding.root)
        setupUI()
        setupListeners()
        observeViewModel()
    }

    protected abstract fun initializeViewBinding(): Binding
    protected abstract fun initializeViewModel(): VM
    protected abstract fun setupUI()
    protected abstract fun setupListeners()
    protected abstract fun observeViewModel()
}


