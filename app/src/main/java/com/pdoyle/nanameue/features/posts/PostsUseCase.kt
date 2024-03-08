package com.pdoyle.nanameue.features.posts

import com.pdoyle.nanameue.app.posts.Post
import com.pdoyle.nanameue.app.posts.PostForm
import com.pdoyle.nanameue.app.posts.PostsRepository
import kotlinx.coroutines.flow.Flow

class PostsUseCase(private val postsRepository: PostsRepository) {

    suspend fun getPosts(): List<Post> {
        return postsRepository.getPosts()
    }

    suspend fun createPost(postSubmit: PostForm) {
        postsRepository.createPost(postSubmit)
    }

    fun listenForNewPosts(): Flow<List<Post>> {
        return postsRepository.listenForNewPosts()
    }


}