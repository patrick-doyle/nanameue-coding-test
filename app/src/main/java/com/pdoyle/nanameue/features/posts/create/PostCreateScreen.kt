package com.pdoyle.nanameue.features.posts.create

import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.pdoyle.nanameue.features.common.LoadingDialog

@Composable
fun PostCreateScreen(postCreateViewModel: PostCreateViewModel) {

    val snackbarHostState = remember { SnackbarHostState() }

    val postCreateViewState by postCreateViewModel.uiStateFlow().collectAsState()

    if (postCreateViewState.error) {
        val snackbarMessage: String = stringResource(id = postCreateViewState.errorMessage)
        LaunchedEffect(snackbarMessage) {
            snackbarHostState.showSnackbar(
                message = snackbarMessage,
                duration = SnackbarDuration.Short,
                withDismissAction = true
            )
        }
    } else {
        snackbarHostState.currentSnackbarData?.dismiss()
    }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { innerPadding ->
        if (postCreateViewState.loading) {
            LoadingDialog()
        }
        PostForm(innerPadding,
            onPostSubmit = {
                postCreateViewModel.submitForm(it)
            })
    }
}
