package com.pdoyle.nanameue.app.login

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.pdoyle.nanameue.app.AppScope
import com.pdoyle.nanameue.app.users.User
import com.pdoyle.nanameue.app.users.toAppUser
import com.pdoyle.nanameue.util.emptyString
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Wrapper around the firebase API to map it o the app models and errors so the auth can be changed
 * to other sign up methods in the future.
 */
@AppScope
class FirebaseLoginApi @Inject constructor(private val firebaseAuth: FirebaseAuth) {

    suspend fun login(email: String, password: String): AuthResult<User> {
        return try {
            val task = firebaseAuth.signInWithEmailAndPassword(email, password)
            val firebaseUser = task.await().user!!
            val user = User(
                id = firebaseUser.uid,
                username = firebaseUser.displayName ?: emptyString(),
                email = firebaseUser.email ?: emptyString()
            )
             AuthResult.Success(data = user)
        } catch (exception: Exception) {
            AuthResult.Error(error = getLoginError(exception))
        }
    }

    suspend fun signup(email: String, password: String): AuthResult<User> {
        return try {
            val task = firebaseAuth.createUserWithEmailAndPassword(email, password)
            val firebaseUser = task.await().user!!
            AuthResult.Success(data = firebaseUser.toAppUser())
        } catch (exception: Exception) {
            AuthResult.Error(error = getLoginError(exception))
        }
    }

    fun isLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    private fun getLoginError(exception : Exception): LoginError {
        return when(exception) {
            is FirebaseAuthWeakPasswordException -> LoginError.WeakPassword()
            is FirebaseAuthInvalidCredentialsException -> LoginError.InvalidCredentials()
            is FirebaseAuthUserCollisionException -> LoginError.UserExists()
            else -> LoginError.Generic()
        }
    }

    fun signout() {
        firebaseAuth.signOut()
    }
}