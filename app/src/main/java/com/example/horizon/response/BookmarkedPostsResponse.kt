package com.example.horizon.response

import com.example.horizon.models.UploadedPosts

sealed class BookmarkedPostsResponse {

    data class SuccessBookmarkedPosts(val listOfPosts: ArrayList<UploadedPosts>) : BookmarkedPostsResponse()
    object LoadingBookmarkedPosts : BookmarkedPostsResponse()
    data class ErrorBookmarkedPosts(val errorMsg: String) : BookmarkedPostsResponse()
}