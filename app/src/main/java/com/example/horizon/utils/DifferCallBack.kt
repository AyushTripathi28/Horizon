package com.example.horizon.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.horizon.models.UploadedPosts

class DifferCallBack : DiffUtil.ItemCallback<UploadedPosts>() {
    override fun areItemsTheSame(oldItem: UploadedPosts, newItem: UploadedPosts): Boolean {
        return oldItem.imgUrl == newItem.imgUrl
    }

    override fun areContentsTheSame(oldItem: UploadedPosts, newItem: UploadedPosts): Boolean {
        return oldItem == newItem
    }
}