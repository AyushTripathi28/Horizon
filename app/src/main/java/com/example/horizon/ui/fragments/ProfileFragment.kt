package com.example.horizon.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.transform.CircleCropTransformation
import com.example.horizon.R
import com.example.horizon.adapters.AllPostsAdapter
import com.example.horizon.adapters.FooterAdapter
import com.example.horizon.databinding.FragmentProfileBinding
import com.example.horizon.ui.LoginActivity
import com.example.horizon.utils.CurrentUserDetails
import com.example.horizon.utils.DifferCallBack
import com.example.horizon.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile), AllPostsAdapter.OnPostItemClicked {

    private lateinit var viewBinding: FragmentProfileBinding
    private val viewModel: MainViewModel by viewModels()
    private val adapter by lazy { AllPostsAdapter(DifferCallBack(), this) }

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

        viewBinding.btnEditProfile.setOnClickListener {
            val userProfileBundle = Bundle()
            userProfileBundle.apply {
                putString("name", CurrentUserDetails.userName)
                putString("bio", CurrentUserDetails.userBio)
                putString("profileImg", CurrentUserDetails.userProfileImgUrl)
            }
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment, userProfileBundle)
        }

        viewBinding.btnProfileSignOut.setOnClickListener {
            viewModel.signOutCurrentUserViewModel()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
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
                    placeholder(R.drawable.default_profile_image)
                }
            }else{
                ivProfileImage.load(R.drawable.default_profile_image){
                    transformations(CircleCropTransformation())
                }
            }
        }
    }

    override fun onPostItemClicked(postUrl: String) {
        val bundle = Bundle()
        val postId = postUrl.replace("/", "-")
        bundle.putString("postId", postId)

        findNavController().navigate(R.id.action_profileFragment_to_readPostFragment, bundle)
    }
}