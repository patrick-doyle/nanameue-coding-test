package com.pdoyle.nanameue.features.posts

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pdoyle.nanameue.R
import com.pdoyle.nanameue.features.common.theme.NanameueTheme
import com.pdoyle.nanameue.features.login.LoginActivity
import com.pdoyle.nanameue.features.posts.create.PostCreateScreen
import com.pdoyle.nanameue.features.posts.create.PostCreateViewModel
import com.pdoyle.nanameue.features.posts.create.PostCreateViewModelFactory
import com.pdoyle.nanameue.features.posts.timeline.PostsTimelineScreen
import com.pdoyle.nanameue.features.posts.timeline.PostsTimelineViewModel
import com.pdoyle.nanameue.features.posts.timeline.PostsTimelineViewModelFactory
import com.pdoyle.nanameue.util.appComponent
import javax.inject.Inject

class PostsActivity : ComponentActivity() {

    @Inject
    lateinit var postsCreateViewModelFactory: PostCreateViewModelFactory

    @Inject
    lateinit var postsTimelineViewModelFactory: PostsTimelineViewModelFactory

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appComponent()
            .postsComponent(PostsModule())
            .inject(this)

        val postCreateViewModel = ViewModelProvider(this, postsCreateViewModelFactory)[PostCreateViewModel::class.java]
        val postsTimelineViewModel = ViewModelProvider(this, postsTimelineViewModelFactory)[PostsTimelineViewModel::class.java]

        setContent {
            NanameueTheme {
                val navController = rememberNavController()

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(stringResource(R.string.posts))
                            },
                            actions = {
                                TextButton(onClick = {
                                    postsTimelineViewModel.onLogout()
                                    startActivity(Intent(this@PostsActivity, LoginActivity::class.java))
                                    this@PostsActivity.finish()
                                }) {
                                    Text(
                                        text = stringResource(id = R.string.logout)
                                    )
                                }
                            }
                        )
                    },
                ) { paddingValues ->

                    NavHost(
                        navController = navController,
                        startDestination = "posts",
                        Modifier.padding(paddingValues)
                    ) {
                        composable("posts") {
                            PostsTimelineScreen(navController, postsTimelineViewModel = postsTimelineViewModel)
                        }
                        composable("posts/new") {
                            PostCreateScreen(postCreateViewModel = postCreateViewModel)
                        }

                    }
                }
            }
        }
    }
}
