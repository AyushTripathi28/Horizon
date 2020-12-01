package com.example.horizon.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.example.horizon.MainActivity
import com.example.horizon.databinding.ActivityLoginBinding
import com.example.horizon.response.LoginResponse
import com.example.horizon.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var viewBinding: ActivityLoginBinding

    override fun onStart() {
        super.onStart()
        val currentUser = viewModel.getCurrentUserViewModel()
        if (currentUser != null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.btnLogin.setOnClickListener {
            loginUser()
        }
    }

    private fun loginUser(){
        val email = viewBinding.etEmail.text.toString().trim()
        val password = viewBinding.etPassword.text.toString().trim()

        CoroutineScope(Dispatchers.Main).launch {
            viewModel.loginUserViewModel(email, password).collect {
                when(it){
                    is LoginResponse.LoginLoading -> showLoading()

                    is LoginResponse.LoginSuccess -> {
                        hideLoading()
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                    is LoginResponse.LoginError -> {
                        hideLoading()
                        Snackbar.make(viewBinding.root, "${it.error}", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun showLoading(){
        viewBinding.apply {
            etEmail.isEnabled = false
            etPassword.isEnabled = false
            btnLogin.isEnabled = false
            pbLogin.visibility = View.VISIBLE
        }
    }

    private fun hideLoading(){
        viewBinding.apply {
            etEmail.isEnabled = true
            etPassword.isEnabled = true
            btnLogin.isEnabled = true
            pbLogin.visibility = View.GONE
        }
    }
}