package com.pdoyle.nanameue.features.login

import android.app.Activity
import android.content.Intent
import com.pdoyle.nanameue.features.timeline.TimelineActivity


class LoginActivityUseCase(private val activity: Activity)  {

    fun openTimelineActivity() {
        val intent = Intent(activity, TimelineActivity::class.java)
        activity.startActivity(intent)
        activity.finish()
    }


}