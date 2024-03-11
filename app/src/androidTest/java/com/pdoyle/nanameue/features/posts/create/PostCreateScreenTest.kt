package com.pdoyle.nanameue.features.posts.create

import androidx.compose.ui.test.junit4.createComposeRule
import com.pdoyle.nanameue.app.posts.CreatePostForm
import com.pdoyle.nanameue.util.emptyString
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PostCreateScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val viewModel: PostCreateViewModel = mockk(relaxed = true)
    private lateinit var stateFlow: StateFlow<PostCreateViewState>
    private val viewState = PostCreateViewState()

    @Before
    fun setUp() {
        stateFlow = MutableStateFlow(viewState)
        every { viewModel.uiStateFlow() } returns stateFlow
    }

    @Test
    fun testCreatePostSubmitted() {
        val expectedData = CreatePostForm(
            text = "text post text body",
            imageUrl = emptyString()
        )

        composeTestRule.setContent {
            PostCreateScreen(postCreateViewModel = viewModel)
        }

        PostFormViewRobot(composeTestRule) {
            typePostText("text post text body")
            submitForm()
        }

        verify { viewModel.submitForm(expectedData) }
    }
}