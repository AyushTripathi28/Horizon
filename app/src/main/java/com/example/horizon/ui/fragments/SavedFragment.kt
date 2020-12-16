package com.example.horizon.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.horizon.R
import com.example.horizon.adapters.BookmarkedPostsAdapter
import com.example.horizon.databinding.FragmentSavedBinding
import com.example.horizon.response.BookmarkedPostsResponse
import com.example.horizon.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SavedFragment : Fragment(R.layout.fragment_saved), BookmarkedPostsAdapter.OnSavedItemClicked {

    private val viewModel: MainViewModel by viewModels()
    private val adapter by lazy { BookmarkedPostsAdapter(this) }
    private lateinit var viewBinding: FragmentSavedBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding = FragmentSavedBinding.bind(view)
        setupRecyclerView()
        getBookmarkedPosts()
        swipeToRemoveBookmark()

        viewModel.bookmarkedPost.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })

    }

    private fun getBookmarkedPosts(){
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.getBookmarkedPostsViewModel().collect {
                when(it){
                    is BookmarkedPostsResponse.SuccessBookmarkedPosts -> hideLoading()
                    is BookmarkedPostsResponse.ErrorBookmarkedPosts -> {
                        hideLoading()
                        Snackbar.make(viewBinding.root, it.errorMsg, Snackbar.LENGTH_SHORT).show()
                    }
                    is BookmarkedPostsResponse.LoadingBookmarkedPosts -> showLoading()
                }
            }
        }
    }

    private fun swipeToRemoveBookmark(){
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.absoluteAdapterPosition
                val postImgUrl = adapter.currentList[position].imgUrl
                viewModel.removePostFromBookmarkedViewModel(postImgUrl)
                getBookmarkedPosts()
                Snackbar.make(viewBinding.root, "Post removed", Snackbar.LENGTH_SHORT).show()
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(viewBinding.rvBookmarkedPosts)
        }

    }

    private fun setupRecyclerView(){
        viewBinding.rvBookmarkedPosts.layoutManager = LinearLayoutManager(requireContext())
        viewBinding.rvBookmarkedPosts.adapter = adapter
    }

    override fun savedItemClicked(imgUrl: String) {
        val postId = imgUrl.replace("/", "-")
        val bundle = Bundle().apply {
            putString("postId", postId)
        }
        findNavController().navigate(R.id.action_savedFragment_to_readPostFragment, bundle)
    }

    private fun hideLoading(){
        viewBinding.pbSavedPosts.visibility = View.GONE
    }

    private fun showLoading(){
        viewBinding.pbSavedPosts.visibility = View.VISIBLE
    }
}