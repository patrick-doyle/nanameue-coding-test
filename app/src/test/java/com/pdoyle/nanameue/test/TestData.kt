package com.pdoyle.nanameue.test

import com.pdoyle.nanameue.app.posts.Post
import com.pdoyle.nanameue.app.users.User
import java.time.LocalDateTime
import kotlin.random.Random

object TestData {

    const val USER_1_ID = "mock_user_1_id"
    const val USER_2_ID = "mock_user_2_id"

    const val POST_1_ID = "mock_post_1_id"
    const val POST_2_ID = "mock_post_2_id"
    const val POST_3_ID = "mock_post_3_id"
    const val POST_4_ID = "mock_post_4_id"

    const val EMAIL = "test@example.com"
    const val INVALID_EMAIL = "test.at.example.com"
    const val PASSWORD = "password"
    const val INVALID_PASSWORD = ""

    fun posts() = listOf(
        createPost(POST_1_ID, USER_1_ID),
        createPost(POST_2_ID, USER_1_ID),
        createPost(POST_3_ID, USER_2_ID),
        createPost(POST_4_ID, USER_2_ID),
    )

    fun createPost(id: String = POST_1_ID, userId: String = USER_1_ID) = Post(
        id = id,
        author = createUser(userId),
        text = "post text",
        postedAt = LocalDateTime.now().minusHours(Random.nextLong(18)),
        imageUrl = null
    )

    fun createUser(id: String = USER_1_ID) = User(
        id = id,
        username = "username1",
        email = "test1@example.com"
    )
}