package com.pdoyle.nanameue.features.login

import com.pdoyle.nanameue.app.login.LoginError
import com.pdoyle.nanameue.features.login.view.LoginView
import com.pdoyle.nanameue.util.AppDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginCoordinator(
    private val view: LoginView,
    private val scope: CoroutineScope,
    private val dispatchers: AppDispatchers,
    private val loginUseCase: LoginUseCase,
    private val activityUseCase: LoginActivityUseCase
) {

    fun onCreate() {
        scope.launch {
            listenForFormSubmission()
        }
    }

    private suspend fun listenForFormSubmission() {
        view.listenForFormSubmission()
            .onEach {
                withContext(dispatchers.io()) {
                    val authResult = loginUseCase.login(submit = it)
                    withContext(dispatchers.main()) {
                        if (authResult.user != null) {
                            activityUseCase.openTimelineActivity()
                        } else if (authResult.error != null) {
                            view.showLoginError(authResult.error)
                        } else {
                            view.showLoginError(LoginError.Generic())
                        }
                    }
                }
            }
            .launchIn(scope)
    }
}
