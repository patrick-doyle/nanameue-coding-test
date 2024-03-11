package com.pdoyle.nanameue.features.login

import com.pdoyle.nanameue.app.login.AuthResult
import com.pdoyle.nanameue.app.login.LoginError
import com.pdoyle.nanameue.app.login.LoginRepository
import com.pdoyle.nanameue.app.users.User
import javax.inject.Inject

@LoginScope
class LoginUseCase @Inject constructor(private val loginRepository: LoginRepository) {

    fun isLoggedIn(): Boolean {
        return loginRepository.isLoggedIn()
    }

    suspend fun login(submit: LoginFormSubmit): AuthResult<User> {
        return loginRepository.login(submit.email, submit.password)
    }

    suspend fun signup(submit: LoginFormSubmit): AuthResult<User> {
        return loginRepository.signup(submit.email, submit.password)
    }

    fun validate(submit: LoginFormSubmit): AuthResult<Boolean> {
        val validEmailResult = loginRepository.validateEmail(submit.email)
        if(!validEmailResult) {
            return AuthResult.Error(LoginError.MalformedEmail())
        }
        val validPasswordResult = loginRepository.validatePassword(submit.password)
        if(!validPasswordResult) {
            return AuthResult.Error(LoginError.MalformedPassword())
        }
        return AuthResult.Success(true)
    }

}