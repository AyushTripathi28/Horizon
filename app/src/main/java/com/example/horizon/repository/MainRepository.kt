package com.example.horizon.repository


import android.util.Log
import com.example.horizon.response.LoginResponse
import com.example.horizon.response.SignUpResponse
import com.example.horizon.utils.CurrentUserDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MainRepository @Inject constructor(
        private val auth: FirebaseAuth,
        private val fireStore: FirebaseFirestore
) {

    fun getCurrentUserRepository() = auth.currentUser

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
        val userHashMap = HashMap<String, String>()
        userHashMap["name"] = name
        userHashMap["bio"] = ""
        userHashMap["imageUrl"] = ""
        val userCollectionRef = fireStore.collection("Users")
        userCollectionRef.add(userHashMap).await()
        CurrentUserDetails.userName = name
        CurrentUserDetails.userUid = auth.currentUser?.uid.toString()
        emit(SignUpResponse.SignUpSuccess("User successfully signed up"))

    }.catch {
        emit(SignUpResponse.SignUpError("Something went wrong"))
    }
}