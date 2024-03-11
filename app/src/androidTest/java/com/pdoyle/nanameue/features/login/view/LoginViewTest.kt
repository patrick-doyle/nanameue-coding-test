package com.pdoyle.nanameue.features.login.view

import androidx.compose.ui.test.junit4.createComposeRule
import com.google.common.truth.Truth
import com.pdoyle.nanameue.features.login.LoginFormSubmit
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoginViewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val expectedFormData = LoginFormSubmit(
        email = "test@example.com",
        password = "password123",
    )

    private lateinit var loginView: LoginView

    @Before
    fun setUp() {
        loginView = LoginView()
    }

    @Test
    fun setLoginSubmission() {
        val events = mutableListOf<LoginFormSubmit>()

        loginView.listenForLoginSubmission().listen {
            events.add(it)
        }

        composeTestRule.setContent {
            loginView.Compose()
        }

        LoginViewRobot(composeTestRule) {
            inputEmail("test@example.com")
            inputPassword("password123")

            clickLogin()
        }

        Truth.assertThat(events.first()).isEqualTo(expectedFormData)
    }

    @Test
    fun setSignUpSubmission() {
        val events = mutableListOf<LoginFormSubmit>()

        loginView.listenForSignUpSubmission().listen {
            events.add(it)
        }

        composeTestRule.setContent {
            loginView.Compose()
        }

        LoginViewRobot(composeTestRule) {
            inputEmail("test@example.com")
            inputPassword("password123")

            clickSubmit()
        }

        Truth.assertThat(events.first()).isEqualTo(expectedFormData)
    }
}