package com.example.horizon.response

sealed class SignUpResponse{
    data class SignUpSuccess(val msg: String) : SignUpResponse()
    object SignUpLoading : SignUpResponse()
    data class SignUpError(val error: String) : SignUpResponse()
}
