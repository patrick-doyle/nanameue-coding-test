package com.pdoyle.nanameue.app.login

sealed class LoginError(val message: String) {
    class Generic : LoginError("Failed to login")
    class UserExists : LoginError("Email already registered")
    class InvalidCredentials : LoginError("Invalid Credentials")
    class WeakPassword : LoginError("Weak Password")
    class MalformedPassword : LoginError("Password cannot be blank")
    class MalformedEmail : LoginError("Email cannot be malformed")
}