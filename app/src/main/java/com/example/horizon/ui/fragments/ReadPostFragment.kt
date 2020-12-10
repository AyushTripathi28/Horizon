package com.example.horizon.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.load
import com.example.horizon.R
import com.example.horizon.databinding.FragmentReadPostBinding
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding = FragmentReadPostBinding.bind(view)

        val postId = arguments?.get("postId").toString()
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.getPostViewModel(postId).collect {
                when(it){
                    is PostRetrieveResponse.PostRetrieveSuccessful -> {
                        hideLoading()
                        viewBinding.apply {
                            tvTitleBlog.text = it.post.title
                            tvAuthorNameBlog.text = it.post.author
                            ivImageBlog.load(it.post.imgUrl) {
                                placeholder(R.drawable.ic_baseline_image_24)
                            }
                            tvCreatedAtBlog.text = UtilFunctions.timeInMillisToDateFormat(it.post.createdAt)
                            tvContentBlog.text = it.post.content
                            val totalLikes = "${ it.post.likedBy.size } hearts"
                            cbLikedBlog.text = totalLikes
                            if (it.post.likedBy.contains(CurrentUserDetails.userUid)) {
                                cbLikedBlog.isChecked = true
                            }
                        }
                    }
                    is PostRetrieveResponse.PostRetrieveError -> {
                        hideLoading()
                        Snackbar.make(view, it.error, Snackbar.LENGTH_SHORT).show()
                    }
                    is PostRetrieveResponse.PostRetrieveLoading -> showLoading()
                }
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