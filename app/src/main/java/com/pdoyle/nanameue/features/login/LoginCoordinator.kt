package com.pdoyle.nanameue.features.login

import com.pdoyle.nanameue.app.login.AuthResult
import com.pdoyle.nanameue.features.login.view.LoginView
import com.pdoyle.nanameue.util.AppDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

class LoginCoordinator(
    private val view: LoginView,
    private val scope: CoroutineScope,
    private val dispatchers: AppDispatchers,
    private val loginUseCase: LoginUseCase,
    private val activityUseCase: LoginActivityUseCase
) {

    fun onCreate() {
        if(loginUseCase.isLoggedIn()) {
            activityUseCase.openTimelineActivity()
            activityUseCase.finish()
            return
        }
        listenForLoginSubmission()
        listenForSignupSubmission()
    }

    private fun listenForLoginSubmission() {
        view.listenForLoginSubmission()
            .onEach {
                val submissionIsValid = loginUseCase.validate(it)
                if (submissionIsValid is AuthResult.Error) {
                    view.showLoginError(submissionIsValid.error)
                    return@onEach
                }

                val authResult = withContext(dispatchers.io()) {
                    loginUseCase.login(submit = it)
                }

                when (authResult) {
                    is AuthResult.Success -> {
                        activityUseCase.openTimelineActivity()
                        activityUseCase.finish()
                    }
                    is AuthResult.Error -> view.showLoginError(authResult.error)
                }
            }
            .launchIn(scope)
    }

    private fun listenForSignupSubmission() {
        view.listenForSignUpSubmission()
            .onEach {
                val submissionIsValid = loginUseCase.validate(it)
                if (submissionIsValid is AuthResult.Error) {
                    view.showLoginError(submissionIsValid.error)
                    return@onEach
                }

                val authResult = withContext(dispatchers.io()) {
                    loginUseCase.signup(submit = it)
                }

                when (authResult) {
                    is AuthResult.Success -> activityUseCase.openTimelineActivity()
                    is AuthResult.Error -> view.showLoginError(authResult.error)
                }
            }
            .launchIn(scope)
    }
}
