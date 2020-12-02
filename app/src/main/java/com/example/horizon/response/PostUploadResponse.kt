package com.example.horizon.response

sealed class PostUploadResponse{
    data class PostUploadSuccess(val msg: String) : PostUploadResponse()
    object PostUploadLoading : PostUploadResponse()
    data class PostUploadError(val error: String) : PostUploadResponse()
}
