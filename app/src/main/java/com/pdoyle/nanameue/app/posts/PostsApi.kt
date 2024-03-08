package com.pdoyle.nanameue.app.posts

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.pdoyle.nanameue.app.AppScope
import com.pdoyle.nanameue.app.users.userFromFireBaseDoc
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import okhttp3.internal.closeQuietly
import timber.log.Timber
import javax.inject.Inject


/**
 * Maps the posts to the firestore
 */
private const val POST_COLLECTION_NAME = "posts"

@AppScope
class PostsApi @Inject constructor(
    private val contentResolver: ContentResolver,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
) {

    suspend fun getPosts(): List<Post> {
        val result = firestore.collection(POST_COLLECTION_NAME)
            .orderBy("time_posted", Query.Direction.DESCENDING)
            .get()
            .await()

        return result.documents.map {
            val user = userFromFireBaseDoc(
                it.getDocumentReference("author_id")?.get()?.await()!!
            )
            postFromFirebaseDoc(it, user)
        }
    }

    suspend fun makePost(post: PostSubmit) {

        val uploadedFileUrl = if(post.imageUrl != null) {
            val uri = Uri.parse(post.imageUrl)!!

            val cursor = contentResolver.query(uri, null, null, null, null) ?: return
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            val filename = cursor.getString(nameIndex)
            cursor.close()

            val imageStream = contentResolver.openInputStream(uri)!!
            val imagesRef: StorageReference = storage.reference.child(filename)

            val uploadTask = imagesRef.putStream(imageStream)
            val result = uploadTask.await()
            imageStream.closeQuietly()
            result.storage.downloadUrl.await().toString()
        } else {
            null
        }

        val data = hashMapOf(
            "time_posted" to Timestamp.now(),
            "text" to post.text,
            "image_link" to uploadedFileUrl,
            "author_id" to firestore.document("users/${post.authorId}")
        )

        firestore.collection(POST_COLLECTION_NAME)
            .document()
            .set(data)
            .await()
    }

    fun listenForNewPosts(): Flow<List<Post>> {
        return callbackFlow {
            val listenerRegistration = firestore.collection(POST_COLLECTION_NAME)
                .orderBy("time_posted", Query.Direction.DESCENDING)
                .addSnapshotListener { value, error ->
                    if (error != null || value == null) {
                        Timber.w("listen:error", error)
                        return@addSnapshotListener
                    }

                    val posts = mutableListOf<Post>()
                    value.documents.map { postDoc ->
                        postDoc.getDocumentReference("author_id")?.get()
                            ?.addOnSuccessListener { userDoc ->
                                val user = userFromFireBaseDoc(userDoc)
                                val post = postFromFirebaseDoc(postDoc, user)
                                posts.add(post)
                            }
                    }
                    posts.sortByDescending { it.postedAt }
                    trySendBlocking(posts)
                }
            awaitClose { listenerRegistration.remove() }

        }

    }
}