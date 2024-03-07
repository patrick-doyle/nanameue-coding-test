package com.pdoyle.nanameue.app.login

import android.util.Patterns
import com.pdoyle.nanameue.app.AppScope
import com.pdoyle.nanameue.app.users.User
import com.pdoyle.nanameue.app.users.UsersRepository
import javax.inject.Inject

sealed class AuthResult<T> {

    data class Success<T>(val data: T) : AuthResult<T>()

    data class Error<T>(val error: LoginError) : AuthResult<T>()
}

@AppScope
class LoginService @Inject constructor(
    private val loginApi: FirebaseLoginApi,
    private val usersRepository: UsersRepository
) {

    suspend fun login(email: String, password: String): AuthResult<User> {
        return loginApi.login(email, password)
    }

    fun isLoggedIn(): Boolean {
        return loginApi.isLoggedIn()
    }

    suspend fun signup(email: String, password: String): AuthResult<User> {
        val result = loginApi.signup(email, password)
        //this should be a firebase cloud function to create a firestore record for users but
        //that needs a paid plan
        if(result is AuthResult.Success) {
            usersRepository.createUserInStore(result.data)
        }
        return result
    }

    fun validateEmail(email: String): AuthResult<Boolean> {
        val valid = email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
        return if (valid) {
            AuthResult.Success(true)
        } else {
            AuthResult.Error(LoginError.MalformedEmail())
        }
    }

    fun validatePassword(password: String): AuthResult<Boolean> {
        return if (password.isNotBlank()) {
            AuthResult.Success(true)
        } else {
            AuthResult.Error(LoginError.MalformedPassword())
        }
    }

    fun logout() {
        loginApi.signout()
    }
}