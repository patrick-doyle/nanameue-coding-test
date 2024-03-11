package com.pdoyle.nanameue.features.login.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pdoyle.nanameue.R
import com.pdoyle.nanameue.app.login.LoginError
import com.pdoyle.nanameue.features.login.LoginFormSubmit
import com.pdoyle.nanameue.features.login.LoginScope
import com.pdoyle.nanameue.util.EventStream
import com.pdoyle.nanameue.util.emptyString
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@LoginScope
class LoginView @Inject constructor() {

    //flows for user interaction
    private val onLoginEvents = EventStream<LoginFormSubmit>()
    private val onSignupEvents = EventStream<LoginFormSubmit>()

    //updatable state
    private val _snackBarTextId = mutableIntStateOf(0)

    @Composable
    @Preview
    fun Compose() {
        var formData: LoginFormSubmit by remember {
            mutableStateOf(LoginFormSubmit(emptyString(), emptyString()))
        }

        val snackbarHostState = remember { SnackbarHostState() }

        val snackBarTextId: Int by _snackBarTextId
        if (_snackBarTextId.intValue != 0) {
            val snackbarMessage: String = stringResource(id = snackBarTextId)
            LaunchedEffect(snackbarMessage) {
                snackbarHostState.showSnackbar(
                    message = snackbarMessage,
                    duration = SnackbarDuration.Short,
                    withDismissAction = true
                )
                _snackBarTextId.intValue = 0
            }
        }

        MaterialTheme {
            Scaffold(
                snackbarHost = {
                    SnackbarHost(hostState = snackbarHostState)
                },
            ) { innerPadding ->
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    Text(
                        text = stringResource(R.string.login_title),
                        modifier = Modifier.fillMaxWidth(0.9f)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                    EmailField(
                        label = stringResource(R.string.email),
                        value = formData.email,
                        modifier = Modifier.fillMaxWidth(0.9f)
                            .testTag("email_input")
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        onChange = { formData = formData.copy(email = it) },
                    )
                    PasswordField(
                        label = stringResource(R.string.password),
                        value = formData.password,
                        modifier = Modifier.fillMaxWidth(0.9f)
                            .testTag("password_input")
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        onChange = { formData = formData.copy(password = it) },
                        submit = {
                            onLoginEvents.sendEvent(formData)
                        }
                    )
                    TextButton(
                        onClick = {
                            onLoginEvents.sendEvent(formData)
                        },
                        modifier = Modifier.fillMaxWidth(0.9f)
                            .testTag("login_button")
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.login)
                        )
                    }
                    TextButton(
                        onClick = {
                            onSignupEvents.sendEvent(formData)
                        },
                        modifier = Modifier.fillMaxWidth(0.9f)
                            .testTag("signup_button")
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.sign_up)
                        )
                    }
                }
            }
        }
    }

    fun listenForLoginSubmission(): EventStream<LoginFormSubmit> {
        return onLoginEvents
    }

    fun listenForSignUpSubmission(): EventStream<LoginFormSubmit> {
        return onSignupEvents
    }

    fun showLoginError(error: LoginError) {
        _snackBarTextId.intValue = when (error) {
            is LoginError.UserExists -> R.string.login_error_cred_wrong
            is LoginError.InvalidCredentials -> R.string.login_error_cred_wrong
            is LoginError.WeakPassword -> R.string.login_error_weak_password
            is LoginError.MalformedEmail -> R.string.login_error_bad_email
            is LoginError.MalformedPassword -> R.string.login_error_bad_password
            is LoginError.Generic -> R.string.login_error_generic
        }
    }
}
