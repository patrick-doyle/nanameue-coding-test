package com.pdoyle.nanameue.app.posts

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.pdoyle.nanameue.app.AppScope
import com.pdoyle.nanameue.app.users.User
import com.pdoyle.nanameue.app.users.UsersApi
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Maps the posts to the firestore
 */
private const val POST_COLLECTION_NAME = "posts"
@AppScope
class PostsApi @Inject constructor(private val firestore: FirebaseFirestore,
                                   private val usersApi: UsersApi) {

    suspend fun getPosts(): List<Post> {
        val result = firestore.collection(POST_COLLECTION_NAME)
            .get()
            .await()

        return result.documents.map {
            val user = usersApi.getUserForId(it.getDocumentReference("author_id")?.id!!)
            postFromFirebaseDoc(it, user)
        }
    }

    suspend fun makePost(post: PostSubmit)  {
        val data = hashMapOf(
            "time_posted" to Timestamp.now(),
            "text" to post.text,
            "image_link" to post.imageUrl,
            "author_id" to firestore.document("users/${post.authorId}")
        )

        val result = firestore.collection(POST_COLLECTION_NAME)
            .document()
            .set(data)
            .await()
    }
}