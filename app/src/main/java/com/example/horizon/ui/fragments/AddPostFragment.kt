package com.example.horizon.ui.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.horizon.R
import com.example.horizon.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddPostFragment : Fragment(R.layout.fragment_write_blog) {

    private val viewModel: MainViewModel by viewModels()
}