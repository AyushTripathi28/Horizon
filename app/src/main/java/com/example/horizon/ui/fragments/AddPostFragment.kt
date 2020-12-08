package com.example.horizon.ui.fragments

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.horizon.R
import com.example.horizon.databinding.FragmentWriteBlogBinding
import com.example.horizon.response.PostUploadResponse
import com.example.horizon.utils.Constants
import com.example.horizon.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddPostFragment : Fragment(R.layout.fragment_write_blog) {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var viewBinding: FragmentWriteBlogBinding
    private var imgUri: Uri? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding = FragmentWriteBlogBinding.bind(view)

        viewBinding.ivPostImageNewPost.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
            }
            startActivityForResult(intent, Constants.GET_IMAGE_REQUEST_CODE)
        }

        viewBinding.ivPostImageNewPost.setOnClickListener {

        }

        viewBinding.btnCancel.setOnClickListener {
            if (viewBinding.etTitleNewPost.text.isNotEmpty() ||
                viewBinding.etContentNewPost.text.isNotEmpty() ||
                imgUri != null){
                createCancelDialog()
            }else{
                findNavController().popBackStack()
            }

        }

        viewBinding.btnUpload.setOnClickListener { btn ->
            val title = viewBinding.etTitleNewPost.text.toString()
            val content = viewBinding.etContentNewPost.text.toString()
            CoroutineScope(Dispatchers.Main).launch {
                viewModel.uploadNewPostViewModel(title, content, imgUri).collect {
                    when(it){
                        is PostUploadResponse.PostUploadLoading -> showLoading()
                        is PostUploadResponse.PostUploadError -> {
                            hideLoading()
                            Snackbar.make(btn, it.error, Snackbar.LENGTH_SHORT).show()
                        }
                        is PostUploadResponse.PostUploadSuccess ->{
                            hideLoading()
                            findNavController().popBackStack()
                        }
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.GET_IMAGE_REQUEST_CODE && resultCode == RESULT_OK){
            imgUri = data?.data
            viewBinding.ivPostImageNewPost.setImageURI(imgUri)
        }
    }

    private fun createCancelDialog(){
        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Confirm Cancel?")
            .setMessage("Everything written will be lost")
            .setNegativeButton("No"){dialogInterface: DialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .setPositiveButton("Yes"){ _, _ ->
                findNavController().popBackStack()
            }
            .create()

        alertDialog.show()
    }

    private fun hideLoading(){
        viewBinding.apply {
            pbNewPost.visibility = View.GONE
            btnCancel.isEnabled = true
            btnUpload.isEnabled = true
            etTitleNewPost.isEnabled = true
            etContentNewPost.isEnabled = true
            ivPostImageNewPost.isEnabled = true
        }
    }

    private fun showLoading(){
        viewBinding.apply {
            pbNewPost.visibility = View.VISIBLE
            btnCancel.isEnabled = false
            btnUpload.isEnabled = false
            etTitleNewPost.isEnabled = false
            etContentNewPost.isEnabled = false
            ivPostImageNewPost.isEnabled = false
        }
    }

}