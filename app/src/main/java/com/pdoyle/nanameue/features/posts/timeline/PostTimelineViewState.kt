package com.pdoyle.nanameue.features.posts.timeline

import com.pdoyle.nanameue.app.posts.Post
import com.pdoyle.nanameue.app.users.User

data class PostTimelineViewState(
    val posts: List<Post> = emptyList(),
    val currentUser: User? = null,
    val loading: Boolean = false) {

}