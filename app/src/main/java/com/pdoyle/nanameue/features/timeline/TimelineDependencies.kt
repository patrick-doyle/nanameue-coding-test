package com.pdoyle.nanameue.features.timeline

import androidx.appcompat.app.AppCompatActivity
import com.pdoyle.nanameue.features.timeline.view.TimelineView
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import javax.inject.Scope

@Scope
annotation class TimelineScope

@TimelineScope
@Subcomponent(modules = [TimelineModule::class])
interface TimelineComponent {

    fun inject(timelineActivity: TimelineActivity)
}

@Module
class TimelineModule(private val activity: AppCompatActivity) {

    @Provides
    @TimelineScope
    fun timelineUseCase() =
        TimelineUseCase(activity)


    @Provides
    @TimelineScope
    fun viewModel(timelineUseCase: TimelineUseCase) = TimelineViewModel(timelineUseCase)


    @Provides
    @TimelineScope
    fun view() = TimelineView(activity)


}