package com.example.horizon.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.horizon.R
import com.example.horizon.databinding.IndividualPostItemBinding
import com.example.horizon.models.UploadedPosts
import com.example.horizon.utils.UtilFunctions

class AllPostsAdapter(
    diffUtilCallback: DiffUtil.ItemCallback<UploadedPosts>)
    : PagingDataAdapter<UploadedPosts, AllPostsAdapter.AllPostsViewHolder>(diffUtilCallback) {

    class AllPostsViewHolder(val viewBinding: IndividualPostItemBinding) : RecyclerView.ViewHolder(viewBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllPostsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.individual_post_item,parent, false)
        val viewBinding = IndividualPostItemBinding.bind(view)
        return AllPostsViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: AllPostsViewHolder, position: Int) {
        val currentPost = getItem(position)
        val totalClaps = "${currentPost?.likedBy?.size} hearts"
        val postCreatedDate = UtilFunctions.timeInMillisToDateFormat(currentPost?.createdAt)

        holder.viewBinding.apply {
            tvPostTitleAllPosts.text = currentPost?.title
            tvContentPreviewAllPosts.text = currentPost?.content
            tvAuthorAllPosts.text = currentPost?.author
            tvPostCreatedAllPosts.text = postCreatedDate
            tvTotalHeartsAllPosts.text = totalClaps
            ivPostImageAllPosts.load(currentPost?.imgUrl){
                placeholder(R.drawable.ic_baseline_image_24)
            }
        }

        Log.d("AllPostFragment", "On bind func, title is: ${getItem(position)?.title}")
    }
}