package com.pdoyle.nanameue.features.posts.create

import androidx.compose.ui.test.junit4.createComposeRule
import com.google.common.truth.Truth
import com.pdoyle.nanameue.app.posts.CreatePostForm
import com.pdoyle.nanameue.util.emptyString
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PostFormViewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testCreatePostSubmitted() {
        val expectedData = CreatePostForm(
            text = "text post text body",
            imageUrl = emptyString()
        )
        val events = mutableListOf<CreatePostForm>()

        composeTestRule.setContent {
            PostForm(onPostSubmit = { events.add(it) })
        }

        PostFormViewRobot(composeTestRule) {
            typePostText("text post text body")
            submitForm()
        }

        Truth.assertThat(events.first()).isEqualTo(expectedData)
    }
}