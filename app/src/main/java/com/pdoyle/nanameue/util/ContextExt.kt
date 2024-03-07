package com.pdoyle.nanameue.util

import android.app.Activity
import com.pdoyle.nanameue.App
import com.pdoyle.nanameue.app.AppComponent

fun Activity.appComponent() : AppComponent {
    return  App.get(this).getAppComponent()
}