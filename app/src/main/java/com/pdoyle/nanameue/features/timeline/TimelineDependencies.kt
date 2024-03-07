package com.pdoyle.nanameue.features.timeline

import android.app.Activity
import com.pdoyle.nanameue.app.posts.PostsRepository
import com.pdoyle.nanameue.util.AppDispatchers
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import javax.inject.Scope

@Scope
annotation class TimelineScope

@TimelineScope
@Subcomponent(modules = [TimelineModule::class])
interface TimelineComponent {

    fun inject(postsActivity: PostsActivity)
}

@Module
class TimelineModule(private val activity: Activity) {

    @Provides
    @TimelineScope
    fun timelineUseCase(postsRepository: PostsRepository) =
        PostsUseCase(postsRepository)


    @Provides
    @TimelineScope
    fun viewModel(
        postsUseCase: PostsUseCase,
        appDispatchers: AppDispatchers
    ) =
        PostsTimelineViewModel(postsUseCase, appDispatchers)

}