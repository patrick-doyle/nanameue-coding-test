package com.pdoyle.nanameue.features.posts.create

import android.app.Activity
import com.pdoyle.nanameue.features.posts.PostScope
import javax.inject.Inject


@PostScope
class ImagePickUseCase @Inject constructor(activity: Activity) {

    suspend fun pickAndUploadImage(): String {
        return ""
    }
}