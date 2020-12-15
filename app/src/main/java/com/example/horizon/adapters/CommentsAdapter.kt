package com.example.horizon.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.horizon.R
import com.example.horizon.databinding.IndividualCommentItemBinding
import com.example.horizon.models.CommentsModel
import com.example.horizon.utils.UtilFunctions

class CommentsAdapter : ListAdapter<CommentsModel, CommentsAdapter.CommentViewHolder>(diffUtilCallBack) {

    companion object{
        val diffUtilCallBack = object : DiffUtil.ItemCallback<CommentsModel>() {

            override fun areItemsTheSame(oldItem: CommentsModel, newItem: CommentsModel): Boolean {
                Log.d("Adapter", "Comments: areItemsTheSame - $oldItem and $newItem")
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: CommentsModel, newItem: CommentsModel): Boolean {
                Log.d("Adapter", "Comments: ${oldItem.userId}_${oldItem.commentedAt} == ${newItem.userId}_${newItem.commentedAt}")
                return "${oldItem.userId}_${oldItem.commentedAt}" == "${newItem.userId}_${newItem.commentedAt}"
            }
        }
    }

    class CommentViewHolder(val viewBinding: IndividualCommentItemBinding) : RecyclerView.ViewHolder(viewBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val viewBinding = IndividualCommentItemBinding.inflate(layoutInflater)
        Log.d("Adapter", "Comments: OnCreateViewHolder")
        return CommentViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val commentItem = getItem(position)
        Log.d("Adapter", "Comments: Current item in binding: $commentItem")
        holder.viewBinding.apply {
            if (commentItem.userProfileImg != ""){
                ivCommentUserImg.load(commentItem.userProfileImg){
                    transformations(CircleCropTransformation())
                    placeholder(R.drawable.default_profile_image)
                }
            }else{
                ivCommentUserImg.load(R.drawable.default_profile_image){
                    transformations(CircleCropTransformation())
                }
            }

            tvCommentUserName.text = commentItem.userName
            tvCommentCreatedAt.text = UtilFunctions.timeInMillisToDateFormat(commentItem.commentedAt)
            tvComment.text = commentItem.comment
        }
    }
}