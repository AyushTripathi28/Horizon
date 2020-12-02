package com.example.horizon.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.example.horizon.R

class AllPostsFragment : Fragment(R.layout.fragment_all_posts) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("AllPostFragment", "Created")
    }
}