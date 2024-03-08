package com.pdoyle.nanameue.features.login

import android.app.Activity
import android.content.Intent
import com.pdoyle.nanameue.features.posts.PostsActivity


class LoginActivityUseCase(private val activity: Activity)  {

    fun openTimelineActivity() {
        val intent = Intent(activity, PostsActivity::class.java)
        activity.startActivity(intent)
    }

    fun finish() {
        activity.finish()
    }


}