package com.pdoyle.nanameue.app.users

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.pdoyle.nanameue.util.emptyString

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

fun FirebaseUser.toAppUser(): User {
    return User(
        id = uid,
        username = displayName ?: emptyString(),
        email = email ?: emptyString()
    )
}

fun userFromFireBaseDoc(doc: DocumentSnapshot): User {
    return User(
        id = doc.id,
        username = doc.getString("username")!!,
        email = doc.getString("email")!!
    )
}