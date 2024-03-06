package com.pdoyle.nanameue.app.users

data class User(
    val id: String,
    val username: String,
    val email: String) {

    fun getDisplayName(): String {
        if(username.isNotBlank()) {
            return username
        }

        if(email.isNotBlank()) {
            return email
        }
        return id
    }
}