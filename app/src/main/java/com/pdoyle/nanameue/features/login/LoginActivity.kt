package com.pdoyle.nanameue.features.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.ui.tooling.preview.Preview
import com.pdoyle.nanameue.App
import com.pdoyle.nanameue.features.login.view.LoginView
import javax.inject.Inject

class LoginActivity : AppCompatActivity() {

    @Inject
    lateinit var view: LoginView

    @Inject
    lateinit var presenter: LoginCoordinator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.get(this).getAppComponent()
            .loginComponent(LoginModule(this))
            .inject(this)


        setContent {
            view.Compose()
        }
        presenter.onCreate()
    }
}
