package com.pdoyle.nanameue.features.posts

import android.net.ConnectivityManager
import com.pdoyle.nanameue.app.login.LoginRepository
import com.pdoyle.nanameue.app.posts.PostsRepository
import com.pdoyle.nanameue.features.posts.create.PostCreateViewModelFactory
import com.pdoyle.nanameue.features.posts.timeline.PostsTimelineViewModelFactory
import com.pdoyle.nanameue.util.AppDispatchers
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import javax.inject.Scope

@Scope
annotation class PostScope

@PostScope
@Subcomponent(modules = [PostsModule::class])
interface PostComponent {
    fun inject(postsActivity: PostsActivity)
}

@Module
class PostsModule {

    @Provides
    @PostScope
    fun postsUseCase(
        postsRepository: PostsRepository,
        connectivityManager: ConnectivityManager
    ) = PostsUseCase(postsRepository, connectivityManager)

    @Provides
    @PostScope
    fun postsCreateViewModelFactory(
        postsUseCase: PostsUseCase,
        appDispatchers: AppDispatchers
    ) = PostCreateViewModelFactory(postsUseCase, appDispatchers)


    @Provides
    @PostScope
    fun postsTimelineViewModelFactory(
        postsUseCase: PostsUseCase,
        appDispatchers: AppDispatchers,
        loginRepository: LoginRepository
    ) = PostsTimelineViewModelFactory(postsUseCase, appDispatchers, loginRepository)

}