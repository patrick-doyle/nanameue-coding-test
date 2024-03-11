package com.pdoyle.nanameue.features.login

import android.app.Activity
import com.pdoyle.nanameue.features.posts.PostsActivity


class LoginActivityUseCase(private val activity: Activity)  {

    fun openTimelineActivity() {
        PostsActivity.start(activity)
    }

    fun finish() {
        activity.finish()
    }


}