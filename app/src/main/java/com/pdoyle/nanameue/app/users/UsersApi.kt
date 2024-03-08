package com.pdoyle.nanameue.app.users

import com.google.firebase.firestore.FirebaseFirestore
import com.pdoyle.nanameue.app.AppScope
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val USER_COLLECTION_NAME = "users"

@AppScope
class UsersApi @Inject constructor(private val firestore: FirebaseFirestore) {

    suspend fun getUsers(): List<User> {
        return firestore.collection(USER_COLLECTION_NAME)
            .get().await().map {
                userFromFireBaseDoc(it)
            }
    }

    suspend fun getUserForId(userId: String): User {
        val doc = firestore.collection(USER_COLLECTION_NAME)
            .document(userId)
            .get().await()
        return userFromFireBaseDoc(doc)

    }

    suspend fun createUserInStore(user: User) {
        val map = hashMapOf(
            "id" to user.id,
            "username" to user.username,
            "email" to user.email
        )
        firestore.collection(USER_COLLECTION_NAME)
            .document(user.id)
            .set(map)
            .await()
    }

}