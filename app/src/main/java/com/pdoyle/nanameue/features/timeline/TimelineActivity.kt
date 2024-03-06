package com.pdoyle.nanameue.features.timeline

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pdoyle.nanameue.App
import com.pdoyle.nanameue.features.timeline.view.TimelineView
import javax.inject.Inject

class TimelineActivity : AppCompatActivity() {

    @Inject
    lateinit var view: TimelineView

    @Inject
    lateinit var presenter: TimelineViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.get(this).getAppComponent()
            .timelineComponent(TimelineModule(this))
            .inject(this)

        setContentView(view)
        presenter.onCreate()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }
}
