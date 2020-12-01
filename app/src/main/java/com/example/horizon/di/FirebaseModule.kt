package com.example.horizon.di

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object FirebaseModule {

    @Singleton
    @Provides
    fun providesFirebaseAuth() = Firebase.auth

    @Singleton
    @Provides
    fun providesFirebaseFireStore() = Firebase.firestore
}