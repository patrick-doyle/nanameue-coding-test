package com.pdoyle.nanameue.features.login

import com.pdoyle.nanameue.app.login.AuthResult
import com.pdoyle.nanameue.app.login.LoginService
import com.pdoyle.nanameue.app.users.User
import javax.inject.Inject

@LoginScope
class LoginUseCase @Inject constructor(private val loginService: LoginService) {

    fun isLoggedIn(): Boolean {
        return loginService.isLoggedIn()
    }

    suspend fun login(submit: LoginFormSubmit): AuthResult<User> {
        return loginService.login(submit.email, submit.password)
    }

    suspend fun signup(submit: LoginFormSubmit): AuthResult<User> {
        return loginService.signup(submit.email, submit.password)
    }

    fun validate(submit: LoginFormSubmit): AuthResult<Boolean> {
        val validEmailResult = loginService.validateEmail(submit.email)
        if(validEmailResult is AuthResult.Error) {
            return validEmailResult
        }
        val validPasswordResult = loginService.validatePassword(submit.password)
        if(validPasswordResult is AuthResult.Error) {
            return validPasswordResult
        }
        return AuthResult.Success(true)
    }

}