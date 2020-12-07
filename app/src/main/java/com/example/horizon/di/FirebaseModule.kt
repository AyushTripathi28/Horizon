package com.example.horizon.di

import com.example.horizon.utils.AllPostsPagingSource
import com.example.horizon.utils.ParticularUserDataSource
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
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

    @Singleton
    @Provides
    fun providesFirebaseStorage() = Firebase.storage

    @Singleton
    @Provides
    fun providesAllPostsPagingSource(firestore: FirebaseFirestore) = AllPostsPagingSource(firestore.collection("AllPosts"))

    @Singleton
    @Provides
    fun providesParticularUserPostsPagingSource(firestore: FirebaseFirestore) = ParticularUserDataSource(firestore.collection("AllPosts"))
}