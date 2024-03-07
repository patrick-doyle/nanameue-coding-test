package com.pdoyle.nanameue.features.timeline

import com.pdoyle.nanameue.app.posts.Post
import com.pdoyle.nanameue.app.posts.PostsRepository

class PostsUseCase(private val postsRepository: PostsRepository) {

    suspend fun getPosts(): List<Post> {
        return postsRepository.getPosts()
    }


}