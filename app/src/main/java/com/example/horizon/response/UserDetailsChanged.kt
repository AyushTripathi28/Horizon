package com.example.horizon.response

sealed class UserDetailsChanged {
    data class ChangeSuccessful(val msg: String) : UserDetailsChanged()
    object ChangeLoading : UserDetailsChanged()
    data class ChangeError(val error: String) : UserDetailsChanged()
}