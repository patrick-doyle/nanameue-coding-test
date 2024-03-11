package com.pdoyle.nanameue.features.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
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

    companion object {
        fun start(activity: Activity) {
            activity.startActivity(Intent(activity, LoginActivity::class.java))
        }
    }
}
