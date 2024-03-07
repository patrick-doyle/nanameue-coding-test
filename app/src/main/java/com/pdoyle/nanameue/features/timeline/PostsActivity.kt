package com.pdoyle.nanameue.features.timeline

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pdoyle.nanameue.App
import com.pdoyle.nanameue.features.common.theme.NanameueTheme
import com.pdoyle.nanameue.features.timeline.view.PostsTimelineView
import com.pdoyle.nanameue.util.appComponent
import javax.inject.Inject

class PostsActivity : ComponentActivity() {

    @Inject
    lateinit var postsTimelineViewModel: PostsTimelineViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appComponent()
            .timelineComponent(TimelineModule(this))
            .inject(this)

        setContent {
            NanameueTheme {
                PostsTimelineView(postsTimelineViewModel = postsTimelineViewModel)
            }
        }
    }
}
