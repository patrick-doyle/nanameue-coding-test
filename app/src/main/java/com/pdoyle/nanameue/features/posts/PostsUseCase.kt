package com.pdoyle.nanameue.features.posts

import android.net.ConnectivityManager
import com.pdoyle.nanameue.app.posts.Post
import com.pdoyle.nanameue.app.posts.PostForm
import com.pdoyle.nanameue.app.posts.PostsRepository
import kotlinx.coroutines.flow.Flow


class PostsUseCase(
    private val postsRepository: PostsRepository,
    private val connectivityManager: ConnectivityManager
) {

    suspend fun getPosts(): List<Post> {
        return postsRepository.getPosts()
    }

    suspend fun createPost(postSubmit: PostForm) {
        postsRepository.createPost(postSubmit)
    }

    fun listenForNewPosts(): Flow<List<Post>> {
        return postsRepository.listenForNewPosts()
    }

    fun isConnected(): Boolean {
        return try {
            val networkInfo = connectivityManager.activeNetworkInfo
            networkInfo != null && networkInfo.isConnected
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

}