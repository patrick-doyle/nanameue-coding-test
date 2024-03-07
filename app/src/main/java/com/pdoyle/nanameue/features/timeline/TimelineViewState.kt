package com.pdoyle.nanameue.features.timeline

import com.pdoyle.nanameue.app.posts.Post
import com.pdoyle.nanameue.app.users.User

data class TimelineViewState(
    val posts: List<Post> = emptyList(),
    val currentUser: User? = null)