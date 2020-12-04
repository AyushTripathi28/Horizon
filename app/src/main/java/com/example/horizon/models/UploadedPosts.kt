package com.example.horizon.models

data class UploadedPosts(
    var title: String = "",
    var imgUrl: String = "",
    var content: String = "",
    var author: String = "",
    var authorId: String = "",
    var createdAt: Long = 0L,
    var likedBy: ArrayList<String> = arrayListOf()
)
