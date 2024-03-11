package com.pdoyle.nanameue.app.login

import androidx.core.util.PatternsCompat
import com.pdoyle.nanameue.app.AppScope
import com.pdoyle.nanameue.app.users.User
import com.pdoyle.nanameue.app.users.UsersRepository
import javax.inject.Inject

sealed class AuthResult<T> {

    data class Success<T>(val data: T) : AuthResult<T>()

    data class Error<T>(val error: LoginError) : AuthResult<T>()
}

@AppScope
class LoginRepository @Inject constructor(
    private val loginApi: LoginApi,
    private val usersRepository: UsersRepository
) {

    suspend fun login(email: String, password: String): AuthResult<User> {
        return loginApi.login(email, password)
    }

    fun isLoggedIn(): Boolean {
        return loginApi.isLoggedIn()
    }

    fun currentUser(): User {
        return loginApi.currentUser()
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

    fun validateEmail(email: String): Boolean  {
        return email.isNotBlank() && PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun validatePassword(password: String): Boolean {
        return password.isNotBlank()
    }

    fun logout() {
        loginApi.signout()
    }
}