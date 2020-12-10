package com.example.horizon.repository


import android.net.Uri
import android.util.Log
import com.example.horizon.models.CurrentUser
import com.example.horizon.models.UploadedPosts
import com.example.horizon.response.*
import com.example.horizon.utils.AllPostsPagingSource
import com.example.horizon.utils.CurrentUserDetails
import com.example.horizon.utils.ParticularUserDataSource
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
        private val allPostsPagingSource: AllPostsPagingSource,
        private val particularUserDataSource: ParticularUserDataSource
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

    suspend fun getPostRepository(postId: String) = flow<PostRetrieveResponse> {
        val post = allPostCollectionRef.document(postId).get().await().toObject(UploadedPosts::class.java)
        emit(PostRetrieveResponse.PostRetrieveSuccessful(post!!))
    }.catch {error ->
        Log.d("MainRepo", "getPostRepository error: ${error.localizedMessage}")
        emit(PostRetrieveResponse.PostRetrieveError("Something went wrong"))
    }

    fun getAllPostsRepository() = allPostsPagingSource

    fun getParticularUserPostsRepository(userId: String) : ParticularUserDataSource{
        particularUserDataSource.userId = userId
        return  particularUserDataSource
    }

    suspend fun changeUserProfileRepository(newName: String, newBio: String, newImage: Uri?, removeProfileImgMsg: String) = flow<UserDetailsChanged> {
        val oldName = CurrentUserDetails.userName
        val oldBio = CurrentUserDetails.userBio
        val detailsChangedHashMap = HashMap<String, String>()
        if (oldName!=newName){
            detailsChangedHashMap["name"] = newName
        }
        if (oldBio!=newBio){
            detailsChangedHashMap["bio"] = newBio
        }

        newImage?.let {
            val storageRefChild = storageRef.child("${CurrentUserDetails.userUid}.jpg")
            storageRefChild.putFile(it).await()
            val imageDownloadUrl = storageRefChild.downloadUrl.await().toString()
            detailsChangedHashMap["imageUrl"] = imageDownloadUrl
        }

        if (removeProfileImgMsg == "remove"){
            detailsChangedHashMap["imageUrl"] = ""
            storageRef.child("${CurrentUserDetails.userUid}.jpg").delete().await()
        }

        userCollectionRef.document(CurrentUserDetails.userUid).set(detailsChangedHashMap, SetOptions.merge()).await()
        emit(UserDetailsChanged.ChangeSuccessful("Details changed"))
        if (newName != oldName){
            val allPostsOfAuthor = allPostCollectionRef.whereEqualTo("authorId", CurrentUserDetails.userUid).get().await()
            for(posts in allPostsOfAuthor.documents){
                val postDocumentId = posts.id
                allPostCollectionRef.document(postDocumentId).set(hashMapOf(Pair("author", newName)), SetOptions.merge())
            }
        }

    }.catch {e ->
        Log.d("MainRepo", "Changing profile details error: ${e.localizedMessage}")
        emit(UserDetailsChanged.ChangeError("Something went wrong"))
    }
}