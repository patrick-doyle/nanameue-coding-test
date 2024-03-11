package com.pdoyle.nanameue.features.login.view

import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput

class LoginViewRobot(
    private val composeTestRule: ComposeTestRule,
    block: LoginViewRobot.() -> Unit
) {

    init {
        this.block()
    }

    fun inputEmail(email: String) {
        composeTestRule.onNodeWithTag("email_input")
            .performTextInput(email)
    }

    fun inputPassword(password: String) {
        composeTestRule.onNodeWithTag("password_input")
            .performTextInput(password)
    }

    fun clickLogin() {
        composeTestRule.onNodeWithTag("login_button")
            .performClick()
    }

    fun clickSubmit() {
        composeTestRule.onNodeWithTag("signup_button")
            .performClick()
    }
}