package com.example.horizon.ui.bottomfragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.horizon.adapters.CommentsAdapter
import com.example.horizon.databinding.FragmentCommentBinding
import com.example.horizon.viewmodel.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CommentsFragment : BottomSheetDialogFragment() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var viewBinding: FragmentCommentBinding
    private val adapter by lazy { CommentsAdapter() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewBinding = FragmentCommentBinding.inflate(inflater)
        setupRecyclerView()
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val imgUrl = arguments?.get("imgUrl").toString()
        viewModel.getCommentsOfPostViewModel(imgUrl)

        viewModel.postComment.observe(viewLifecycleOwner, {
            Log.d("CommentFragment", "Comments: Comment list observed is: $it")
            it?.let {
                adapter.submitList(it)
            }
        })

        viewBinding.btnPostComment.setOnClickListener {
            val comment = viewBinding.etComment.text.toString()
            if (comment.isEmpty()){
                Toast.makeText(requireContext(), "Write comment first", Toast.LENGTH_SHORT).show()
            }else {
                viewModel.postCommentViewModel(comment, imgUrl)
                viewBinding.etComment.setText("")
            }
        }
    }

    private fun setupRecyclerView(){
        viewBinding.rvComments.layoutManager = LinearLayoutManager(requireContext())
        viewBinding.rvComments.adapter = adapter
        ViewCompat.setNestedScrollingEnabled(viewBinding.rvComments, true)
    }
}