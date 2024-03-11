package com.pdoyle.nanameue.features.posts.create

import app.cash.turbine.test
import com.google.common.truth.Truth
import com.pdoyle.nanameue.R
import com.pdoyle.nanameue.TestAppDispatchers
import com.pdoyle.nanameue.app.posts.CreatePostForm
import com.pdoyle.nanameue.features.posts.PostScreenNav
import com.pdoyle.nanameue.features.posts.PostsUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
class PostCreateViewModelTest {

    private val dispatchers = TestAppDispatchers()
    private val scope: TestScope = TestScope(dispatchers.getTestDispatcher())

    private val postUseCase: PostsUseCase = mockk(relaxed = true)
    private val postScreenNav: PostScreenNav = mockk(relaxed = true)

    private lateinit var viewModel: PostCreateViewModel

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(dispatchers.getTestDispatcher())

        every { postUseCase.isConnected() } returns true
        viewModel = PostCreateViewModel(postUseCase, postScreenNav, dispatchers)
    }

    @Test
    fun submitForm() = scope.runTest {
        //GIVEN
        val stateAfterSubmit = PostCreateViewState(loading = false,
            error = false, errorMessage = 0)
        val submit = CreatePostForm("post_text", null)

        //WHEN
        viewModel.submitForm(submit)

        //THEN
        advanceUntilIdle()
        viewModel.uiStateFlow().test(timeout = 50.milliseconds) {
           Truth.assertThat(awaitItem()).isEqualTo(stateAfterSubmit)
        }

        verify { postScreenNav.navigateBack() }
    }

    @Test
    fun submitFormNotConnected() = scope.runTest {
        //GIVEN
        every { postUseCase.isConnected() } returns false
        val stateAfterSubmit = PostCreateViewState(loading = false,
            error = true, errorMessage = R.string.post_error_connection)
        val submit = CreatePostForm("post_text", null)

        //WHEN
        viewModel.submitForm(submit)

        //THEN
        advanceUntilIdle()
        viewModel.uiStateFlow().test(timeout = 50.milliseconds) {
           Truth.assertThat(awaitItem()).isEqualTo(stateAfterSubmit)
        }
    }

    @Test
    fun submitFormInvalid() = scope.runTest {
        //GIVEN
        val stateAfterSubmit = PostCreateViewState(loading = false,
            error = true, errorMessage = R.string.post_error_form)
        val submit = CreatePostForm(null, null)

        //WHEN
        viewModel.submitForm(submit)

        //THEN
        advanceUntilIdle()
        viewModel.uiStateFlow().test(timeout = 50.milliseconds) {
           Truth.assertThat(awaitItem()).isEqualTo(stateAfterSubmit)
        }
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }
}