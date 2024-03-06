package com.pdoyle.nanameue.app.login

import com.pdoyle.nanameue.app.AppScope
import com.pdoyle.nanameue.app.users.User
import javax.inject.Inject

data class AuthResult(val user: User? = null, val error: LoginError? = null)

@AppScope
class LoginService @Inject constructor(
    private val loginApi: FirebaseLoginApi) {

    suspend fun login(email: String, password: String): AuthResult {
        return loginApi.login(email, password)
    }

    suspend fun signup(email: String, password: String): AuthResult {
        return loginApi.signup(email, password)
    }

    fun logout() {
        loginApi.signout()
    }
}