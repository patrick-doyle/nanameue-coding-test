package com.pdoyle.nanameue.app.posts

import com.pdoyle.nanameue.app.AppScope
import javax.inject.Inject

@AppScope
class PostsRepository  @Inject constructor(
    private val postsApi: PostsApi) {

    suspend fun getPosts(): List<Post> {
        return postsApi.getPosts()
    }
}