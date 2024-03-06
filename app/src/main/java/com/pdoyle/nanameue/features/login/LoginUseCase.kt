package com.pdoyle.nanameue.features.login

import com.pdoyle.nanameue.app.login.AuthResult
import com.pdoyle.nanameue.app.login.LoginService
import com.pdoyle.nanameue.features.login.view.LoginFormSubmit
import timber.log.Timber
import javax.inject.Inject

@LoginScope
class LoginUseCase @Inject constructor(private val loginService: LoginService) {

    suspend fun login(submit: LoginFormSubmit): AuthResult {
        return loginService.login(submit.email, submit.password)
    }

}