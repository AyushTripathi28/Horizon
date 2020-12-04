package com.example.horizon.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.horizon.R
import com.example.horizon.adapters.AllPostsAdapter
import com.example.horizon.databinding.FragmentAllPostsBinding
import com.example.horizon.utils.DifferCallBack
import com.example.horizon.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AllPostsFragment : Fragment(R.layout.fragment_all_posts) {

    private lateinit var viewBinding: FragmentAllPostsBinding
    private val viewModel: MainViewModel by viewModels()
    private val adapter by lazy { AllPostsAdapter(DifferCallBack()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("AllPostFragment", "Created")
        viewBinding = FragmentAllPostsBinding.bind(view)

        setUpAllPostRecyclerView()
//        viewModel.allPostsLiveData.observe(viewLifecycleOwner,{
//            Log.d("AllPostFragment", "Observing start")
//            CoroutineScope(Dispatchers.Main).launch {
//                Log.d("AllPostFragment", "Just before submitting, the data is $it")
//                adapter.submitData(it)
//            }
//        })

        CoroutineScope(Dispatchers.Main).launch {
            viewModel.allPostsLiveData.collect {
                Log.d("AllPostFragment", "Collection start")
                adapter.submitData(it)
            }
        }
    }

    private fun setUpAllPostRecyclerView(){
        viewBinding.rvAllPosts.layoutManager = LinearLayoutManager(requireContext())
        viewBinding.rvAllPosts.adapter = adapter
    }
}