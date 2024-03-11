package com.pdoyle.nanameue.app.posts

import com.google.firebase.firestore.DocumentSnapshot
import com.pdoyle.nanameue.app.users.User
import com.pdoyle.nanameue.util.emptyString
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

data class Post(
    val id: String,
    val author: User,
    val text: String,
    val postedAt: LocalDateTime,
    val imageUrl: String?
)

data class PostSubmit(
    val authorId: String,
    val text: String?,
    val imageUrl: String?
)

data class CreatePostForm(
    val text: String?,
    val imageUrl: String?
)

fun postFromFirebaseDoc(doc: DocumentSnapshot, user: User): Post {
    val timestamp = doc.getTimestamp("time_posted")?.toDate() ?: Date()
    return Post(id = doc.id,
        author = user,
        text = doc.getString("text") ?: emptyString(),
        imageUrl = doc.getString("image_link") ?: emptyString(),
        postedAt = LocalDateTime.ofInstant(
            timestamp.toInstant(), ZoneId.systemDefault())
    )
}