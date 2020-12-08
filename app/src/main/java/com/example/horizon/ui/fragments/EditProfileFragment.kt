package com.example.horizon.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.horizon.R
import com.example.horizon.databinding.FragmentEditProfileBinding
import com.google.android.material.snackbar.Snackbar

class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {

    private lateinit var viewBinding: FragmentEditProfileBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding = FragmentEditProfileBinding.bind(view)
        val userName = arguments?.getString("name")
        val userBio = arguments?.getString("bio")
        val profileImg = arguments?.getString("profileImg")

        Snackbar.make(view, "The bundle was passed of $userName", Snackbar.LENGTH_SHORT).show()
    }
}