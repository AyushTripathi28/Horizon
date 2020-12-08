package com.example.horizon.viewmodel

import android.net.Uri
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.horizon.repository.MainRepository
import com.example.horizon.response.LoginResponse
import com.example.horizon.response.PostUploadResponse
import com.example.horizon.response.SignUpResponse
import com.example.horizon.response.UserDetailsChanged
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class MainViewModel @ViewModelInject constructor(
        private val repository: MainRepository
) : ViewModel() {

    val allPostsLiveData = Pager(PagingConfig(20)){
        repository.getAllPostsRepository()
    }.flow.cachedIn(viewModelScope).asLiveData(viewModelScope.coroutineContext)

    fun getParticularUserPostsViewModel(userId: String) = Pager(PagingConfig(20)){
        repository.getParticularUserPostsRepository(userId)
    }.flow.cachedIn(viewModelScope).asLiveData(viewModelScope.coroutineContext)

    fun getCurrentUserViewModel() = repository.getCurrentUserRepository()

    fun getCurrentUserDetailsViewModel(userUid: String) =  repository.getCurrentUserDetailsRepository(userUid)

    suspend fun loginUserViewModel(email: String, password: String) = flow {
        emit(LoginResponse.LoginLoading)
        if (email.isEmpty() or password.isEmpty()){
            emit(LoginResponse.LoginError("Email or Password can't be left empty"))
        }else{
            withContext(Dispatchers.IO){
                repository.loginUserRepository(email, password).collect {
                    withContext(Dispatchers.Main){
                        emit(it)
                    }
                }
            }
        }
    }

    suspend fun signUpUserViewModel(name: String, email: String, password: String, confirmPassword: String) = flow {
        emit(SignUpResponse.SignUpLoading)
        when {
            name.isEmpty() or email.isEmpty() or password.isEmpty() or confirmPassword.isEmpty() -> {
                emit(SignUpResponse.SignUpError("None of the fields can be left empty"))
            }
            password != confirmPassword -> {
                Log.d("SignUpViewModel", "password is $password and confirm is $confirmPassword")
                emit(SignUpResponse.SignUpError("Passwords doesn't match"))
            }
            else -> {
                withContext(Dispatchers.IO){
                    repository.signUpNewUserRepository(name, email, password).collect{
                        withContext(Dispatchers.Main){
                            emit(it)
                        }
                    }
                }
            }
        }
    }

    suspend fun uploadNewPostViewModel(title: String, content: String, imgUri: Uri?) = flow {
        emit(PostUploadResponse.PostUploadLoading)
        when {
            title.isEmpty() or content.isEmpty() -> {
                emit(PostUploadResponse.PostUploadError("Title or content can't be left empty"))
            }
            imgUri == null -> {
                emit(PostUploadResponse.PostUploadError("Please upload an image"))
            }
            else -> {
                withContext(Dispatchers.IO){
                    repository.uploadNewPostRepository(imgUri, title, content).collect {
                        withContext(Dispatchers.Main){
                            emit(it)
                        }
                    }
                }
            }
        }
    }

    suspend fun changeUserProfileViewModel(newName: String, newBio: String, imageUri: Uri?, removeProfileImgMsg:String) = flow {
        emit(UserDetailsChanged.ChangeLoading)
        if (newName.isEmpty()){
            emit(UserDetailsChanged.ChangeError("Name cannot be left empty"))
        }else{
            withContext(Dispatchers.IO){
                repository.changeUserProfileRepository(newName, newBio, imageUri, removeProfileImgMsg).collect{
                    withContext(Dispatchers.Main){
                        emit(it)
                    }
                }
            }
        }
    }
}