package com.example.horizon.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.horizon.R
import com.example.horizon.databinding.IndividualPostItemBinding
import com.example.horizon.models.UploadedPosts
import com.example.horizon.utils.UtilFunctions

class BookmarkedPostsAdapter(private val listener: OnSavedItemClicked) : ListAdapter<UploadedPosts, BookmarkedPostsAdapter.BookmarkedPostsViewHolder>(diffUtilCallback){

    companion object{
        val diffUtilCallback = object : DiffUtil.ItemCallback<UploadedPosts>() {
            override fun areItemsTheSame(oldItem: UploadedPosts, newItem: UploadedPosts): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: UploadedPosts, newItem: UploadedPosts): Boolean {
                return oldItem.imgUrl == newItem.imgUrl
            }
        }
    }
    class BookmarkedPostsViewHolder(val viewBinding: IndividualPostItemBinding) : RecyclerView.ViewHolder(viewBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkedPostsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val viewBinding = IndividualPostItemBinding.inflate(inflater)
        return BookmarkedPostsViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: BookmarkedPostsViewHolder, position: Int) {
        val currentPost = getItem(position)
        val totalHearts = "${currentPost?.likedBy?.size} hearts"
        val postCreatedDate = UtilFunctions.timeInMillisToDateFormat(currentPost?.createdAt)

        holder.viewBinding.apply {
            tvPostTitleAllPosts.text = currentPost?.title
            tvContentPreviewAllPosts.text = currentPost?.content
            tvAuthorAllPosts.text = currentPost?.author
            tvPostCreatedAllPosts.text = postCreatedDate
            tvTotalHeartsAllPosts.text = totalHearts
            ivPostImageAllPosts.load(currentPost?.imgUrl){
                placeholder(R.drawable.ic_baseline_image_24)
            }
        }

        holder.itemView.setOnClickListener {
            currentPost?.imgUrl?.let {
                listener.savedItemClicked(it)
            }
        }
    }

    interface OnSavedItemClicked{
        fun savedItemClicked(imgUrl: String)
    }
}