package com.pdoyle.nanameue.app.posts

import java.time.LocalDateTime

data class Post(
    val id: String,
    val text: String,
    val postedAt: LocalDateTime,
    val imageUrl: String
)