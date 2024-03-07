package com.pdoyle.nanameue.features.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import com.pdoyle.nanameue.features.login.view.LoginView
import com.pdoyle.nanameue.util.appComponent
import javax.inject.Inject

class LoginActivity : AppCompatActivity() {

    @Inject
    lateinit var view: LoginView

    @Inject
    lateinit var presenter: LoginCoordinator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appComponent()
            .loginComponent(LoginModule(this))
            .inject(this)


        setContent {
            view.Compose()
        }
        presenter.onCreate()
    }
}
