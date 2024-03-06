package com.pdoyle.nanameue.app.posts

import com.pdoyle.nanameue.app.AppScope
import javax.inject.Inject

@AppScope
class PostsRepository  @Inject constructor() {

    fun getPosts(): List<Post> {
        return emptyList()
    }
}