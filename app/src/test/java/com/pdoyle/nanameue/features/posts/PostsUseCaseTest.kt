package com.pdoyle.nanameue.features.posts

import com.google.common.truth.Truth
import com.pdoyle.nanameue.app.posts.CreatePostForm
import com.pdoyle.nanameue.app.posts.PostsRepository
import com.pdoyle.nanameue.app.proxy.ConnectivityManagerProxy
import com.pdoyle.nanameue.test.TestData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
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
        val post = CreatePostForm("post_text", "")
        coEvery { repository.createPost(post) } returns Unit

        //WHEN
        useCase.createPost(post)

        //THEN
        coVerify { repository.createPost(post) }
    }

    @Test
    fun listenForNewPosts() = runTest {
        //GIVEN
        val testPosts = TestData.posts()
        val flow = flowOf(testPosts)
        coEvery { repository.listenForNewPosts() } returns flow

        //WHEN
        val resultData = useCase.listenForNewPosts()
            .toList()[0]

        //THEN
        Truth.assertThat(resultData).isEqualTo(testPosts)
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