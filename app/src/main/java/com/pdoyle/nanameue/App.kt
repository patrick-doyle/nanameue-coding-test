package com.pdoyle.nanameue

import android.app.Activity
import android.app.Application
import com.pdoyle.nanameue.app.AppComponent
import com.pdoyle.nanameue.app.AppModule
import com.pdoyle.nanameue.app.DaggerAppComponent
import timber.log.Timber

class App : Application() {

    companion object {
        @JvmStatic
        fun get(activity: Activity) = activity.application as App
    }

    private val component: AppComponent by lazy {
        DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }

    fun getAppComponent() = component
}