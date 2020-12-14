package com.example.horizon.response

sealed class DeleteBlogResponse{
    data class DeleteBlogSuccess(val msg: String) : DeleteBlogResponse()
    object DeleteBlogLoading : DeleteBlogResponse()
    data class DeleteBlogError(val errorMsg: String) : DeleteBlogResponse()
}
