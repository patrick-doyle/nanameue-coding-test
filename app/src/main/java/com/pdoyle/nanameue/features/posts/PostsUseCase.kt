package com.pdoyle.nanameue.features.posts

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
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
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

}