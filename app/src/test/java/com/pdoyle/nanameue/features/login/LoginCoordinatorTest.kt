package com.pdoyle.nanameue.features.login

import com.pdoyle.nanameue.TestAppDispatchers
import com.pdoyle.nanameue.app.login.AuthResult
import com.pdoyle.nanameue.app.login.LoginError
import com.pdoyle.nanameue.features.login.view.LoginView
import com.pdoyle.nanameue.test.TestData
import com.pdoyle.nanameue.util.EventStream
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class LoginCoordinatorTest {


    private val dispatchers = TestAppDispatchers()
    private val scope: TestScope = TestScope(dispatchers.getTestDispatcher())
    private val scheduler = scope.testScheduler

    private val view: LoginView = mockk(relaxed = true)
    private val loginUseCase: LoginUseCase = mockk(relaxed = true)
    private val activityUseCase: LoginActivityUseCase = mockk(relaxed = true)

    private lateinit var loginCoordinator: LoginCoordinator

    @BeforeEach
    fun setUp() {
        every { loginUseCase.isLoggedIn() } returns false
        every { view.listenForSignUpSubmission() } returns EventStream()
        every { view.listenForLoginSubmission() } returns EventStream()

        loginCoordinator = LoginCoordinator(view, scope, dispatchers, loginUseCase, activityUseCase)
    }

    @Test
    fun timelineOpenedWhenLoggedIn() {
        //GIVEN
        every { loginUseCase.isLoggedIn() } returns true

        //WHEN
        loginCoordinator.onCreate()

        //THEN
        verify { activityUseCase.openTimelineActivity() }
        verify { activityUseCase.finish() }
    }

    @Nested
    inner class LoginSubmission {

        private val loginFormSubmit = LoginFormSubmit(TestData.EMAIL, TestData.PASSWORD)
        private val user = TestData.createUser()
        private val eventFlow = EventStream<LoginFormSubmit>()

        @BeforeEach
        fun setUp() {
            every { view.listenForLoginSubmission() } returns eventFlow
            coEvery { loginUseCase.validate(loginFormSubmit) } returns AuthResult.Success(true)
            coEvery { loginUseCase.login(loginFormSubmit) } returns AuthResult.Success(user)
        }

        @Test
        fun listenForLoginSubmission() {
            //WHEN
            loginCoordinator.onCreate()
            eventFlow.sendEvent(loginFormSubmit)
            scheduler.advanceUntilIdle()

            //THEN
            verify { activityUseCase.openTimelineActivity() }
            verify { activityUseCase.finish() }
            verify(exactly = 0) { view.showLoginError(any()) }
        }

        @Test
        fun listenForLogin_SubmissionInvalidForm() {
            //GIVEN
            val error = LoginError.MalformedEmail()
            coEvery { loginUseCase.validate(loginFormSubmit) } returns AuthResult.Error(error)

            //WHEN
            loginCoordinator.onCreate()
            eventFlow.sendEvent(loginFormSubmit)
            scheduler.advanceUntilIdle()

            //THEN
            verify { view.showLoginError(error) }
            verify(exactly = 0) { activityUseCase.openTimelineActivity() }
            verify(exactly = 0) { activityUseCase.finish() }
        }

        @Test
        fun listenForLogin_LoginFailed() = scope.runTest {
            //GIVEN
            val error = LoginError.InvalidCredentials()
            coEvery { loginUseCase.login(loginFormSubmit) } returns AuthResult.Error(error)

            //WHEN
            loginCoordinator.onCreate()
            eventFlow.sendEvent(loginFormSubmit)
            scheduler.advanceUntilIdle()

            //THEN
            verify { view.showLoginError(error) }
        }
    }

    @Nested
    inner class SignUpSubmission {

        private val loginFormSubmit = LoginFormSubmit(TestData.EMAIL, TestData.PASSWORD)
        private val user = TestData.createUser()
        private val eventFlow = EventStream<LoginFormSubmit>()

        @BeforeEach
        fun setUp() {
            every { view.listenForSignUpSubmission() } returns eventFlow
            coEvery { loginUseCase.validate(loginFormSubmit) } returns AuthResult.Success(true)
            coEvery { loginUseCase.signup(loginFormSubmit) } returns AuthResult.Success(user)
        }

        @Test
        fun listenForLoginSubmission() {
            //WHEN
            loginCoordinator.onCreate()
            eventFlow.sendEvent(loginFormSubmit)
            scheduler.advanceUntilIdle()

            //THEN
            verify { activityUseCase.openTimelineActivity() }
            verify { activityUseCase.finish() }
            verify(exactly = 0) { view.showLoginError(any()) }
        }

        @Test
        fun listenForLogin_SubmissionInvalidForm() {
            //GIVEN
            val error = LoginError.MalformedEmail()
            coEvery { loginUseCase.validate(loginFormSubmit) } returns AuthResult.Error(error)

            //WHEN
            loginCoordinator.onCreate()
            eventFlow.sendEvent(loginFormSubmit)
            scheduler.advanceUntilIdle()

            //THEN
            verify { view.showLoginError(error) }
            verify(exactly = 0) { activityUseCase.openTimelineActivity() }
            verify(exactly = 0) { activityUseCase.finish() }
        }

        @Test
        fun listenForLogin_LoginFailed() = scope.runTest {
            //GIVEN
            val error = LoginError.UserExists()
            coEvery { loginUseCase.signup(loginFormSubmit) } returns AuthResult.Error(error)

            //WHEN
            loginCoordinator.onCreate()
            eventFlow.sendEvent(loginFormSubmit)
            scheduler.advanceUntilIdle()

            //THEN
            verify { view.showLoginError(error) }
        }
    }


}