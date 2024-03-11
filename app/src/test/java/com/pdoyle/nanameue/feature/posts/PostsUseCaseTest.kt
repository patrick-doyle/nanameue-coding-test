package com.pdoyle.nanameue.feature.posts

import com.google.common.truth.Truth
import com.pdoyle.nanameue.app.posts.Post
import com.pdoyle.nanameue.app.posts.PostForm
import com.pdoyle.nanameue.app.posts.PostsRepository
import com.pdoyle.nanameue.app.proxy.ConnectivityManagerProxy
import com.pdoyle.nanameue.features.posts.PostsUseCase
import com.pdoyle.nanameue.test.TestData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PostsUseCaseTest {

    private val repository = mockk<PostsRepository>(relaxed = true)
    private val connectivityManager = mockk<ConnectivityManagerProxy>(relaxed = true)


    private lateinit var useCase: PostsUseCase

    @BeforeEach
    fun setUp() {
        useCase = PostsUseCase(repository, connectivityManager)
    }

    @Test
    fun getPosts() = runTest {
        //GIVEN
        val posts = TestData.posts()
        coEvery { useCase.getPosts() } returns posts

        //WHEN
        val resultData = useCase.getPosts()

        //THEN
        Truth.assertThat(resultData).isEqualTo(posts)
    }

    @Test
    fun createPost() = runTest {
        //GIVEN
        val post = PostForm("post_text", "")
        coEvery { repository.createPost(post) } returns Unit

        //WHEN
        useCase.createPost(post)

        //THEN
        coVerify { repository.createPost(post) }
    }

    @Test
    fun listenForNewPosts() {
        //GIVEN
        val flow = emptyFlow<List<Post>>()
        coEvery { repository.listenForNewPosts() } returns flow

        //WHEN
        val resultData = useCase.listenForNewPosts()

        //THEN
        Truth.assertThat(resultData).isEqualTo(flow)
    }

    @Test
    fun isConnected() {
        //GIVEN
        every { connectivityManager.hasConnection() } returns true

        //WHEN
        val isConnected = useCase.isConnected()

        //THEN
        Truth.assertThat(isConnected).isTrue()
    }
}