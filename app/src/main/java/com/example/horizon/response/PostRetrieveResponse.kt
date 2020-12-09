package com.example.horizon.response

import com.example.horizon.models.UploadedPosts

sealed class PostRetrieveResponse {
    data class PostRetrieveSuccessful(val post: UploadedPosts) : PostRetrieveResponse()
    object PostRetrieveLoading : PostRetrieveResponse()
    data class PostRetrieveError(val error: String) : PostRetrieveResponse()
}