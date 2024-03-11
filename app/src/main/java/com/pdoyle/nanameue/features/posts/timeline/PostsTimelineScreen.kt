package com.pdoyle.nanameue.features.posts.timeline


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pdoyle.nanameue.R
import com.pdoyle.nanameue.features.common.LoadingDialog

@Composable
fun PostsTimelineScreen(
    postsTimelineViewModel: PostsTimelineViewModel = viewModel()
) {

    val timelineViewState by postsTimelineViewModel.uiStateFlow().collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { postsTimelineViewModel.onFabClicked() }) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.make_post)
                )
            }
        }

    ) { paddingValues ->

        Box {
            if (timelineViewState.loading) {
                LoadingDialog()
            }

            Column(
                modifier = Modifier
                    .safeDrawingPadding()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                LazyColumn(
                    contentPadding = PaddingValues(bottom=10.dp)
                ) {
                    items(timelineViewState.posts.size) { index ->
                        PostView(timelineViewState.posts[index])
                    }
                }
            }
        }

    }
}