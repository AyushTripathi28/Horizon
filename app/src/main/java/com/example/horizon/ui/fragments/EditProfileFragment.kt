package com.example.horizon.ui.fragments

import android.app.Activity.RESULT_OK
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import coil.transform.CircleCropTransformation
import com.example.horizon.R
import com.example.horizon.databinding.FragmentEditProfileBinding
import com.example.horizon.response.UserDetailsChanged
import com.example.horizon.utils.Constants
import com.example.horizon.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {

    private lateinit var viewBinding: FragmentEditProfileBinding
    private val viewModel: MainViewModel by viewModels()
    private var imageUri: Uri? = null
    private var removeProfileImgMsg = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding = FragmentEditProfileBinding.bind(view)
        val userName = arguments?.getString("name")
        val userBio = arguments?.getString("bio")
        val profileImg = arguments?.getString("profileImg")

        //Setting up the current profile details to edit
        viewBinding.apply {

            if (profileImg != ""){
                ivProfileImageEdit.load(profileImg){
                    transformations(CircleCropTransformation())
                }
            }else{
                ivProfileImageEdit.load(R.drawable.default_profile_image){
                    transformations(CircleCropTransformation())
                }
            }
            etNameProfileEdit.setText(userName)
            etBioProfileEdit.setText(userBio)
        }

        viewBinding.btnCancelChangedDetails.setOnClickListener {
            createAlertCancelDialogue()
        }

        viewBinding.btnSaveChangedDetails.setOnClickListener {
            changeUserProfile(it)
        }

        viewBinding.btnRemoveProfileImgEdit.setOnClickListener {
            imageUri = null
            removeProfileImgMsg = "remove"
            viewBinding.ivProfileImageEdit.load(R.drawable.default_profile_image){
                transformations(CircleCropTransformation())
            }
        }

        viewBinding.btnChangeProfileImageEdit.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
            }

            startActivityForResult(intent, Constants.GET_PROFILE_IMAGE_CODE)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.GET_PROFILE_IMAGE_CODE && resultCode == RESULT_OK){
            data?.data?.let {
                cropProfileImage(it)
            }

        }else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            imageUri = CropImage.getActivityResult(data).uri
            imageUri?.let {
                viewBinding.ivProfileImageEdit.load(imageUri){
                    transformations(CircleCropTransformation())
                }
            }
        }
    }

    private fun cropProfileImage(uri: Uri){
        CropImage.activity(uri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(4,4)
            .start(requireContext(), this)
    }

    private fun changeUserProfile(view: View){
        val name = viewBinding.etNameProfileEdit.text.toString()
        val bio = viewBinding.etBioProfileEdit.text.toString()

        CoroutineScope(Dispatchers.Main).launch {
            viewModel.changeUserProfileViewModel(name, bio, imageUri, removeProfileImgMsg).collect {
                when(it){
                    is UserDetailsChanged.ChangeLoading -> showLoading()
                    is UserDetailsChanged.ChangeError -> {
                        hideLoading()
                        Snackbar.make(view, it.error, Snackbar.LENGTH_SHORT).show()
                    }
                    is  UserDetailsChanged.ChangeSuccessful -> {
                        hideLoading()
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }

    private fun createAlertCancelDialogue(){
        AlertDialog.Builder(requireContext())
                .setTitle("Do you want to cancel?")
                .setMessage("All the changes will be lost. Do you want to continue?")
                .setNegativeButton("No"){ dialogInterface: DialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                .setPositiveButton("Yes"){ _, _ ->
                    findNavController().popBackStack()
                }
                .create()
                .show()
    }

    private fun showLoading(){
        viewBinding.apply {
            btnCancelChangedDetails.isEnabled = false
            btnSaveChangedDetails.isEnabled = false
            btnChangeProfileImageEdit.isEnabled = false
            btnRemoveProfileImgEdit.isEnabled = false
            etNameProfileEdit.isEnabled = false
            etBioProfileEdit.isEnabled = false
            pbEditProfile.visibility = View.VISIBLE
        }
    }

    private fun hideLoading(){
        viewBinding.apply {
            btnCancelChangedDetails.isEnabled = true
            btnSaveChangedDetails.isEnabled = true
            btnChangeProfileImageEdit.isEnabled = true
            btnRemoveProfileImgEdit.isEnabled = true
            etNameProfileEdit.isEnabled = true
            etBioProfileEdit.isEnabled = true
            pbEditProfile.visibility = View.GONE
        }
    }
}