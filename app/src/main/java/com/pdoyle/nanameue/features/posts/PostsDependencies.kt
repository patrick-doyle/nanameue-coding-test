package com.pdoyle.nanameue.features.posts

import com.pdoyle.nanameue.app.login.LoginRepository
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
class PostsModule(private val postsActivity: PostsActivity) {

    @Provides
    @PostScope
    fun postScreenNav() = PostScreenNav(postsActivity)

    @Provides
    @PostScope
    fun postsCreateViewModelFactory(
        postsUseCase: PostsUseCase,
        postScreenNav: PostScreenNav,
        appDispatchers: AppDispatchers
    ) = PostCreateViewModelFactory(postsUseCase, postScreenNav, appDispatchers)


    @Provides
    @PostScope
    fun postsTimelineViewModelFactory(
        postsUseCase: PostsUseCase,
        appDispatchers: AppDispatchers,
        postScreenNav: PostScreenNav,
        loginRepository: LoginRepository
    ) = PostsTimelineViewModelFactory(
        postsUseCase,
        postScreenNav,
        appDispatchers,
        loginRepository
    )

    @Provides
    @PostScope
    fun postScreenViewModelFactory(postScreenNav: PostScreenNav) =
        PostScreenViewModelFactory(postScreenNav)

}