package com.example.horizon.ui

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.activity.viewModels
import com.example.horizon.MainActivity
import com.example.horizon.databinding.ActivitySignupBinding
import com.example.horizon.response.SignUpResponse
import com.example.horizon.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {

    private lateinit var viewbinding: ActivitySignupBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewbinding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(viewbinding.root)
        setLoginSpan()

        viewbinding.btnSignUp.setOnClickListener {
            signUpUser()
        }

        viewbinding.tvExistingUser.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun signUpUser(){
        val name = viewbinding.etSignUpName.text.toString().trim()
        val email = viewbinding.etSignUpEmail.text.toString().trim()
        val password = viewbinding.etSignUpPassword.text.toString().trim()
        val confirmPassword = viewbinding.etSignUpConfirmPassword.text.toString().trim()

        CoroutineScope(Dispatchers.Main).launch {
            viewModel.signUpUserViewModel(name, email, password, confirmPassword).collect {
                when(it){
                    is SignUpResponse.SignUpLoading -> showLoading()
                    is SignUpResponse.SignUpError -> {
                        hideLoading()
                        Snackbar.make(viewbinding.root, it.error, Snackbar.LENGTH_SHORT).show()
                    }
                    is SignUpResponse.SignUpSuccess -> {
                        hideLoading()
                        val currentUser = viewModel.getCurrentUserViewModel()
                        viewModel.getCurrentUserDetailsViewModel(currentUser?.uid!!)
                        val intent = Intent(this@SignUpActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }

    private fun setLoginSpan(){
        val loginSpanString = SpannableString("Already have an account? Login")
        val foregroundColorSpan = ForegroundColorSpan(Color.parseColor("#FD5523"))
        loginSpanString.setSpan(foregroundColorSpan, 25, 30, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        viewbinding.tvExistingUser.text = loginSpanString
    }

    private fun showLoading(){
        viewbinding.apply {
            pbSignUp.visibility = View.VISIBLE
            etSignUpName.isEnabled = false
            etSignUpEmail.isEnabled = false
            etSignUpPassword.isEnabled = false
            etSignUpConfirmPassword.isEnabled = false
            tvExistingUser.isEnabled = false
            btnSignUp.isEnabled = false
        }
    }

    private fun hideLoading(){
        viewbinding.apply {
            pbSignUp.visibility = View.GONE
            etSignUpName.isEnabled = true
            etSignUpEmail.isEnabled = true
            etSignUpPassword.isEnabled = true
            etSignUpConfirmPassword.isEnabled = true
            tvExistingUser.isEnabled = true
            btnSignUp.isEnabled = true
        }
    }
}