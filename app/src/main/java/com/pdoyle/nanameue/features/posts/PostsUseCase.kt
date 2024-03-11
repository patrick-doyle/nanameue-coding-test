package com.pdoyle.nanameue.features.posts

import com.pdoyle.nanameue.app.posts.Post
import com.pdoyle.nanameue.app.posts.PostForm
import com.pdoyle.nanameue.app.posts.PostsRepository
import com.pdoyle.nanameue.app.proxy.ConnectivityManagerProxy
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@PostScope
class PostsUseCase @Inject constructor(
    private val postsRepository: PostsRepository,
    private val connectivityManager: ConnectivityManagerProxy
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
        return connectivityManager.hasConnection()
    }

}