package com.pdoyle.nanameue.features.timeline.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.pdoyle.nanameue.R
import com.pdoyle.nanameue.app.posts.Post
import java.time.format.DateTimeFormatter

@Composable
fun PostView(post: Post) {

    ConstraintLayout(
        modifier = Modifier.padding(horizontal = 18.dp, vertical = 4.dp)
    ) {

        val (image, title, date) = createRefs()

        Image(
            painterResource(R.drawable.ic_launcher_background),
            contentDescription = stringResource(id = R.string.image_content),
            Modifier.constrainAs(image) {

            }
        )
        Text(
            text = post.text,
            Modifier.constrainAs(title) {
                top.linkTo(image.bottom, margin = 4.dp)
            }
        )
        Text(
            text = DateTimeFormatter.ISO_DATE_TIME.format(post.postedAt),
            Modifier.constrainAs(date) {
                top.linkTo(title.bottom, margin = 4.dp)
            }
        )
    }
}