package com.pdoyle.nanameue.features.posts.create

import com.pdoyle.nanameue.R

data class PostCreateViewState(
    val loading: Boolean = false,
    val error: Boolean = false,
    val errorMessage: Int = R.string.post_error,
)