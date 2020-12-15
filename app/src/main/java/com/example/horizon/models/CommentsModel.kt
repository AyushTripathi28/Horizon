package com.example.horizon.models

data class CommentsModel(
        var userName: String = "",
        var userId: String = "",
        var userProfileImg: String = "",
        var commentedAt:Long = 0L,
        var comment: String = ""
)