package com.pdoyle.nanameue.features.posts.create

data class PostCreateViewState(
    val loading: Boolean = false,
    val error: Boolean = false,
    val errorMessage: Int = 0,
)