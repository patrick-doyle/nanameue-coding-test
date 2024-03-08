package com.pdoyle.nanameue.app.posts

import com.pdoyle.nanameue.app.AppScope
import com.pdoyle.nanameue.app.login.LoginRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@AppScope
class PostsRepository @Inject constructor(
    private val postsApi: PostsApi,
    private val loginRepository: LoginRepository
) {

    suspend fun getPosts(): List<Post> {
        return postsApi.getPosts()
    }

    suspend fun createPost(postSubmit: PostForm) {
        val currentUser = loginRepository.currentUser()
        postsApi.makePost(PostSubmit(
            authorId = currentUser.id,
            text = postSubmit.text,
            imageUrl = postSubmit.imageUrl
        ))
    }

    fun listenForNewPosts(): Flow<List<Post>> {
        return postsApi.listenForNewPosts()
    }
}