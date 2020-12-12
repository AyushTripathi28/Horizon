package com.example.horizon.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.horizon.R
import com.example.horizon.databinding.FragmentReadPostBinding
import com.example.horizon.models.UploadedPosts
import com.example.horizon.response.PostRetrieveResponse
import com.example.horizon.utils.CurrentUserDetails
import com.example.horizon.utils.UtilFunctions
import com.example.horizon.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReadPostFragment : Fragment(R.layout.fragment_read_post) {

    private lateinit var viewBinding: FragmentReadPostBinding
    private val viewModel: MainViewModel by viewModels()
    private var authorId = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding = FragmentReadPostBinding.bind(view)

        val postId = arguments?.get("postId").toString()
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.getPostViewModel(postId).collect {
                when(it){
                    is PostRetrieveResponse.PostRetrieveSuccessful -> {
                        hideLoading()
                        authorId = it.post.authorId
                    }
                    is PostRetrieveResponse.PostRetrieveError -> {
                        hideLoading()
                        Snackbar.make(view, it.error, Snackbar.LENGTH_SHORT).show()
                    }
                    is PostRetrieveResponse.PostRetrieveLoading -> showLoading()
                }
            }
        }
        viewModel.post.observe(viewLifecycleOwner, {
            displayPost(it)
        })

        viewBinding.tvAuthorNameBlog.setOnClickListener {
            val bundle = Bundle().apply {
                putString("authorId", authorId)
            }
            findNavController().navigate(R.id.action_readPostFragment_to_anotherUserFragment, bundle)
        }
    }

    private fun displayPost(post: UploadedPosts){
        viewBinding.apply {
            tvTitleBlog.text = post.title
            tvAuthorNameBlog.text = post.author
            ivImageBlog.load(post.imgUrl) {
                placeholder(R.drawable.ic_baseline_image_24)
            }
            tvCreatedAtBlog.text = UtilFunctions.timeInMillisToDateFormat(post.createdAt)
            tvContentBlog.text = post.content
            val totalLikes = "${ post.likedBy.size } hearts"
            cbLikedBlog.text = totalLikes
            if (post.likedBy.contains(CurrentUserDetails.userUid)) {
                cbLikedBlog.isChecked = true
            }
            decideLikedOrDisliked(post.imgUrl.replace("/", "-"), post.likedBy)
        }
    }

    private fun decideLikedOrDisliked(postId: String, likedByList: ArrayList<String>){
        viewBinding.cbLikedBlog.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                val totalLikes = "${likedByList.size + 1} hearts"
                buttonView.text = totalLikes
                viewModel.likeDislikePostViewModel(postId, likedByList)
            }else{
                val totalLikes = "${likedByList.size - 1} hearts"
                buttonView.text = totalLikes
                viewModel.likeDislikePostViewModel(postId, likedByList)
            }
        }
    }

    private fun showLoading(){
        viewBinding.pbBlog.visibility = View.VISIBLE
    }

    private fun hideLoading(){
        viewBinding.pbBlog.visibility = View.GONE
    }
}