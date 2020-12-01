package com.example.horizon.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.example.horizon.repository.MainRepository
import com.example.horizon.response.LoginResponse
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
                    when(it){
                        is LoginResponse.LoginSuccess -> {
                            withContext(Dispatchers.Main){
                                emit(it)
                            }
                        }

                        is  LoginResponse.LoginError -> {
                            withContext(Dispatchers.Main){
                                emit(it)
                            }
                        }

                        is LoginResponse.LoginLoading -> {
                            withContext(Dispatchers.Main){
                                emit(it)
                            }
                        }
                    }
                }
            }
        }
    }
}