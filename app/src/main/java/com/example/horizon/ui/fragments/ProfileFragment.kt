package com.example.horizon.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.transform.CircleCropTransformation
import com.example.horizon.R
import com.example.horizon.adapters.AllPostsAdapter
import com.example.horizon.adapters.FooterAdapter
import com.example.horizon.databinding.FragmentProfileBinding
import com.example.horizon.utils.CurrentUserDetails
import com.example.horizon.utils.DifferCallBack
import com.example.horizon.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var viewBinding: FragmentProfileBinding
    private val viewModel: MainViewModel by viewModels()
    private val adapter by lazy { AllPostsAdapter(DifferCallBack()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding = FragmentProfileBinding.bind(view)

        setupPostsRecyclerView()
        setupAdapterProperties()
        setProfileFragment()

        viewModel.getParticularUserPostsViewModel(CurrentUserDetails.userUid).observe(viewLifecycleOwner, {
            CoroutineScope(Dispatchers.Main).launch {
                adapter.submitData(it)
            }
        })
    }

    private fun setupPostsRecyclerView(){
        viewBinding.rvUserPosts.layoutManager = LinearLayoutManager(requireContext())
        ViewCompat.setNestedScrollingEnabled(viewBinding.rvUserPosts, true)
        viewBinding.rvUserPosts.adapter = adapter
    }

    private fun setupAdapterProperties(){
        adapter.withLoadStateFooter(FooterAdapter())
        adapter.addLoadStateListener {
            if (it.refresh == LoadState.Loading){
                viewBinding.pbUserProfilePosts.visibility = View.VISIBLE
            }else{
                viewBinding.pbUserProfilePosts.visibility = View.GONE
            }
        }
    }

    private fun setProfileFragment(){
        viewBinding.apply {
            tvProfileName.text = CurrentUserDetails.userName
            tvProfileBio.text = CurrentUserDetails.userBio
            if (CurrentUserDetails.userProfileImgUrl != ""){
                ivProfileImage.load(CurrentUserDetails.userProfileImgUrl){
                    transformations(CircleCropTransformation())
                }
            }else{
                ivProfileImage.load(R.drawable.default_profile_image){
                    transformations(CircleCropTransformation())
                }
            }
        }
    }
}