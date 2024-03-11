package com.pdoyle.nanameue.features.posts.create

import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput

class PostFormViewRobot(
    private val composeTestRule: ComposeTestRule,
    block: PostFormViewRobot.() -> Unit
) {

    init {
        this.block()
    }

    fun typePostText(text: String) {
        composeTestRule.onNodeWithTag("post_text")
            .performTextInput(text)
    }

    fun submitForm() {
        composeTestRule.onNodeWithTag("create_post")
            .performClick()
    }

}