package com.example.horizon.repository


import android.net.Uri
import android.util.Log
import com.example.horizon.models.CurrentUser
import com.example.horizon.response.LoginResponse
import com.example.horizon.response.PostUploadResponse
import com.example.horizon.response.SignUpResponse
import com.example.horizon.utils.AllPostsPagingSource
import com.example.horizon.utils.CurrentUserDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MainRepository @Inject constructor(
        private val auth: FirebaseAuth,
        fireStore: FirebaseFirestore,
        storage: FirebaseStorage,
        private val allPostsPagingSource: AllPostsPagingSource
) {

    private val userCollectionRef = fireStore.collection("Users")
    private val allPostCollectionRef = fireStore.collection("AllPosts")
    private val storageRef = storage.reference

    fun getCurrentUserRepository() = auth.currentUser

    fun getCurrentUserDetailsRepository(currentUserUid: String){
        userCollectionRef.document(currentUserUid).addSnapshotListener { value, error ->
            error?.let {
                return@addSnapshotListener
            }
            value?.let {
                val currentUserDetails = it.toObject(CurrentUser::class.java)
                CurrentUserDetails.apply {
                    currentUserDetails?.let { currentUser ->
                        this.userName = currentUser.name
                        this.userBio = currentUser.bio
                        this.userProfileImgUrl = currentUser.imageUrl
                    }
                    userUid = currentUserUid
                }
            }
        }
    }
    suspend fun loginUserRepository(email: String, password: String) = flow<LoginResponse>{
        Log.d("MainRepo", "Thread while login is : ${Thread.currentThread().name}")
        auth.signInWithEmailAndPassword(email, password).await()
        emit(LoginResponse.LoginSuccess("Logged in"))
    }.catch {e ->
        Log.d("MainRepo", "Login error message is ${e.message}")
        Log.d("MainRepo", "Login error localized message is ${e.localizedMessage}")
        emit(LoginResponse.LoginError("Check your email and password or try again later."))
    }

    suspend fun signUpNewUserRepository(name: String, email: String, password: String) = flow<SignUpResponse> {
        auth.createUserWithEmailAndPassword(email, password).await()
        val currentUser = CurrentUser(name)
        val currentUserUid = auth.currentUser?.uid
        userCollectionRef.document(currentUserUid!!).set(currentUser, SetOptions.merge()).await()
        CurrentUserDetails.userName = name
        CurrentUserDetails.userUid = auth.currentUser?.uid.toString()
        emit(SignUpResponse.SignUpSuccess("User successfully signed up"))

    }.catch {
        emit(SignUpResponse.SignUpError("Something went wrong"))
    }

    suspend fun uploadNewPostRepository(imgUri: Uri, postTitle: String, postContent: String) = flow<PostUploadResponse> {
        val currentUserId = CurrentUserDetails.userUid
        val authorName = CurrentUserDetails.userName
        val currentTimeInMillis = System.currentTimeMillis()
        val likedBy = arrayListOf<String>()

        val storageRefChild = storageRef.child("${currentUserId}_${currentTimeInMillis}.jpg")
        storageRefChild.putFile(imgUri).await()
        val imgUrl = storageRefChild.downloadUrl.await().toString()

        val newPostHashMap = HashMap<String, Any>()
        newPostHashMap["title"] = postTitle
        newPostHashMap["content"] = postContent
        newPostHashMap["imgUrl"] = imgUrl
        newPostHashMap["author"] = authorName
        newPostHashMap["authorId"] = currentUserId
        newPostHashMap["likedBy"] = likedBy
        newPostHashMap["createdAt"] = currentTimeInMillis

        val postDocId = imgUrl.replace("/", "-")
        allPostCollectionRef.document(postDocId).set(newPostHashMap, SetOptions.merge()).await()
        emit(PostUploadResponse.PostUploadSuccess("Post uploaded"))
    }.catch {error ->
        Log.d("MainRepo", "Image and post update error is : ${error.message}")
        emit(PostUploadResponse.PostUploadError("Something went wrong while uploading"))
    }

    fun getAllPostsRepository() = allPostsPagingSource
}