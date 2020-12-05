package com.example.horizon.utils

import android.util.Log
import androidx.paging.PagingSource
import com.example.horizon.models.UploadedPosts
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class AllPostsPagingSource(private val postsCollectionReference: CollectionReference) : PagingSource<QuerySnapshot, UploadedPosts>() {

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, UploadedPosts> {
        return try {
            val currentPage = params.key?:postsCollectionReference
                .limit(20)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val lastDocumentSnapshot = currentPage.documents[currentPage.size() - 1]

            val nextPage = postsCollectionReference
                .limit(20)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .startAfter(lastDocumentSnapshot)
                .get()
                .await()

            LoadResult.Page(
                currentPage.toObjects(UploadedPosts::class.java),
                null,
                nextPage
            )

        }catch (e: Exception){
            Log.d("AllPostFragment", "Load catch block, the exception is ${e.message}")
            LoadResult.Error(e)
        }
    }
}