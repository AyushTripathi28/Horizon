package com.example.horizon.response

import com.example.horizon.models.CommentsModel

sealed class CommentResponse{
    data class CommentResponseSuccess(val comment: CommentsModel) : CommentResponse()
    data class CommentResponseError(val errorMsg: String) : CommentResponse()
}
