package com.example.horizon.ui.fragments

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
import com.example.horizon.databinding.FragmentAnotherUserBinding
import com.example.horizon.models.CurrentUser
import com.example.horizon.response.UserDetailsResponse
import com.example.horizon.utils.CurrentUserDetails
import com.example.horizon.utils.DifferCallBack
import com.example.horizon.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AnotherUserFragment : Fragment(R.layout.fragment_another_user), AllPostsAdapter.OnPostItemClicked {

    private lateinit var viewBinding: FragmentAnotherUserBinding
    private val viewModel: MainViewModel by viewModels()
    private val adapter by lazy { AllPostsAdapter(DifferCallBack(), this) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding = FragmentAnotherUserBinding.bind(view)
        val authorId = arguments?.get("authorId").toString()

        if (authorId == CurrentUserDetails.userUid){
            viewBinding.btnAnotherEditProfile.visibility = View.VISIBLE
            viewBinding.tvAnotherUserPosts.visibility = View.GONE
        }else{
            viewBinding.btnAnotherEditProfile.visibility = View.GONE
            viewBinding.tvAnotherUserPosts.visibility = View.VISIBLE
        }
        setupRecyclerView()
        setupAdapterProperties()
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.getAnotherUserDetailsViewModel(authorId).collect {
                when(it){
                    is UserDetailsResponse.LoadingUserDetails -> viewBinding.pbAnotherUserProfilePosts.visibility = View.VISIBLE
                    is UserDetailsResponse.ErrorUserDetails -> {
                        viewBinding.pbAnotherUserProfilePosts.visibility = View.GONE
                        Snackbar.make(view, it.errorMsg, Snackbar.LENGTH_SHORT).show()
                    }
                    is UserDetailsResponse.SuccessUserDetails -> {
                        viewBinding.pbAnotherUserProfilePosts.visibility = View.GONE
//                        it.userDetails?.let { user ->
//                            displayProfile(user)
//                        }
                    }
                }
            }
        }

        viewModel.userprofile.observe(viewLifecycleOwner, {
            displayProfile(it)
        })

        viewModel.getParticularUserPostsViewModel(authorId).observe(viewLifecycleOwner, {
            CoroutineScope(Dispatchers.Main).launch{
                adapter.submitData(it)
            }
        })
    }

    private fun displayProfile(userProfile: CurrentUser){
        viewBinding.apply {
            tvAnotherProfileName.text = userProfile.name
            tvAnotherProfileBio.text = userProfile.bio
            if (userProfile.imageUrl != ""){
                ivAnotherProfileImage.load(userProfile.imageUrl){
                    transformations(CircleCropTransformation())
                    placeholder(R.drawable.default_profile_image)
                }
            }else{
                ivAnotherProfileImage.load(R.drawable.default_profile_image){
                    transformations(CircleCropTransformation())
                }
            }
        }

        viewBinding.btnAnotherEditProfile.setOnClickListener {
            val bundle = Bundle().apply {
                putString("name", userProfile.name)
                putString("bio", userProfile.bio)
                putString("profileImg", userProfile.imageUrl)
            }
            findNavController().navigate(R.id.action_anotherUserFragment_to_editProfileFragment, bundle)
        }
    }

    private fun setupRecyclerView(){
        viewBinding.rvAnotherUserPosts.layoutManager = LinearLayoutManager(requireContext())
        ViewCompat.setNestedScrollingEnabled(viewBinding.rvAnotherUserPosts, true)
        viewBinding.rvAnotherUserPosts.adapter = adapter

    }

    private fun setupAdapterProperties(){
        adapter.withLoadStateFooter(FooterAdapter())
        adapter.addLoadStateListener {
            if (it.refresh == LoadState.Loading){
                viewBinding.pbAnotherUserProfilePosts.visibility = View.VISIBLE
            }else{
                viewBinding.pbAnotherUserProfilePosts.visibility = View.GONE
            }
        }
    }

    override fun onPostItemClicked(postUrl: String) {
        val bundle = Bundle()
        val postId = postUrl.replace("/", "-")
        bundle.putString("postId", postId)
        findNavController().navigate(R.id.action_anotherUserFragment_to_readPostFragment, bundle)
    }
}