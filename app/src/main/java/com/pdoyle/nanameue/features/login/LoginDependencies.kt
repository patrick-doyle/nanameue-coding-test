package com.pdoyle.nanameue.features.login

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.pdoyle.nanameue.features.login.view.LoginView
import com.pdoyle.nanameue.util.AppDispatchers
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import javax.inject.Scope

@Scope
annotation class LoginScope

@LoginScope
@Subcomponent(modules = [LoginModule::class])
interface LoginComponent {

    fun inject(finderActivity: LoginActivity)
}

@Module
class LoginModule(private val activity: AppCompatActivity) {

    @Provides
    @LoginScope
    fun coordinator(
        view: LoginView,
        activityUseCase: LoginActivityUseCase,
        appDispatchers: AppDispatchers,
        loginUseCase: LoginUseCase
    ): LoginCoordinator {
        return LoginCoordinator(
            view,
            activity.lifecycleScope,
            appDispatchers,
            loginUseCase,
            activityUseCase
        )
    }

    @Provides
    @LoginScope
    fun activityUseCase() = LoginActivityUseCase(activity)
}