package com.example.horizon.viewmodel

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.example.horizon.repository.MainRepository
import com.example.horizon.response.LoginResponse
import com.example.horizon.response.SignUpResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class MainViewModel @ViewModelInject constructor(
        private val repository: MainRepository
) : ViewModel() {

    fun getCurrentUserViewModel() = repository.getCurrentUserRepository()

    suspend fun loginUserViewModel(email: String, password: String) = flow {
        emit(LoginResponse.LoginLoading)
        if (email.isEmpty() or password.isEmpty()){
            emit(LoginResponse.LoginError("Email or Password can't be left empty"))
        }else{
            withContext(Dispatchers.IO){
                repository.loginUserRepository(email, password).collect {
                    withContext(Dispatchers.Main){
                        when(it){
                            is LoginResponse.LoginSuccess -> emit(it)
                            is  LoginResponse.LoginError -> emit(it)
                            is LoginResponse.LoginLoading -> emit(it)
                        }
                    }
                }
            }
        }
    }

    suspend fun signUpUserViewModel(name: String, email: String, password: String, confirmPassword: String) = flow {
        emit(SignUpResponse.SignUpLoading)
        when {
            name.isEmpty() or email.isEmpty() or password.isEmpty() or confirmPassword.isEmpty() -> {
                emit(SignUpResponse.SignUpError("None of the fields can be left empty"))
            }
            password != confirmPassword -> {
                Log.d("SignUpViewModel", "password is $password and confirm is $confirmPassword")
                emit(SignUpResponse.SignUpError("Passwords doesn't match"))
            }
            else -> {
                withContext(Dispatchers.IO){
                    repository.signUpNewUserRepository(name, email, password).collect{
                        withContext(Dispatchers.Main){
                            when(it){
                                is SignUpResponse.SignUpLoading -> emit(it)
                                is SignUpResponse.SignUpSuccess -> emit(it)
                                is SignUpResponse.SignUpError -> emit(it)
                            }
                        }
                    }
                }
            }
        }
    }
}