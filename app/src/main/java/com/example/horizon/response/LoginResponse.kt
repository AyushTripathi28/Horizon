package com.example.horizon.response

sealed class LoginResponse{
    data class LoginSuccess(val msg: String) : LoginResponse()
    object LoginLoading : LoginResponse()
    data class LoginError(val error: String?) : LoginResponse()
}
