package com.example.horizon.repository


import com.example.horizon.response.LoginResponse
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MainRepository @Inject constructor(
        private val auth: FirebaseAuth
) {

    fun getCurrentUserRepository() = auth.currentUser

    suspend fun loginUserRepository(email: String, password: String) = flow<LoginResponse>{
        auth.signInWithEmailAndPassword(email, password).await()
        emit(LoginResponse.LoginSuccess("Logged in"))
    }.catch {
        emit(LoginResponse.LoginError("Check your email and password or try again later."))
    }
}