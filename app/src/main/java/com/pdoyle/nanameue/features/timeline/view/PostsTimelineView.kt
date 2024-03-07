package com.pdoyle.nanameue.features.timeline.view


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pdoyle.nanameue.R
import com.pdoyle.nanameue.features.timeline.PostsTimelineViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostsTimelineView(postsTimelineViewModel: PostsTimelineViewModel = viewModel()) {

    val timelineViewState by postsTimelineViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.timeline))
                },
                actions = {
                    IconButton(onClick = { postsTimelineViewModel.onLogout() }) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = stringResource(id = R.string.logout)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { postsTimelineViewModel.onNewPost() }) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.make_post)
                )
            }
        }

    ) { paddingValues ->
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