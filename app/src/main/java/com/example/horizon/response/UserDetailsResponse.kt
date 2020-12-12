package com.example.horizon.response

import com.example.horizon.models.CurrentUser

sealed class UserDetailsResponse {
    data class SuccessUserDetails(val userDetails: CurrentUser?) : UserDetailsResponse()
    object LoadingUserDetails : UserDetailsResponse()
    data class ErrorUserDetails(val errorMsg: String) : UserDetailsResponse()
}