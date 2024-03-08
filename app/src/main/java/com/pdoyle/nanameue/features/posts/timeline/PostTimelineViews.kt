package com.pdoyle.nanameue.features.posts.timeline

import android.text.format.DateUtils
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Visibility
import com.pdoyle.nanameue.R
import com.pdoyle.nanameue.app.posts.Post
import java.time.ZoneId

@Composable
fun PostView(post: Post) {

    Card(
        modifier = Modifier
            .padding(horizontal = 18.dp, vertical = 4.dp)
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
        ) {

            val (image, title, dateText, author) = createRefs()

            Image(
                painter = painterResource(R.drawable.ic_launcher_background),
                contentDescription = stringResource(id = R.string.image_content),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(100.dp)
                    .width(100.dp)
                    .constrainAs(image) {
                        visibility = if (post.imageUrl != null) {
                            Visibility.Visible
                        } else {
                            Visibility.Visible
                        }
                    }
            )
            val postedAt = post.postedAt.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000
            val dateFormatted = DateUtils.getRelativeTimeSpanString(
                postedAt,
                System.currentTimeMillis(), 0L, DateUtils.FORMAT_ABBREV_ALL
            )
            Text(
                text = dateFormatted.toString(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.constrainAs(dateText) {
                    start.linkTo(image.end, margin = 4.dp)
                }
            )
            Text(
                text = post.author.getDisplayName(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Start,
                modifier = Modifier.constrainAs(author) {
                    start.linkTo(dateText.end, margin = 2.dp)
                    end.linkTo(parent.end, margin = 2.dp)
                    bottom.linkTo(dateText.bottom)
                }
            )
            Text(
                text = post.text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.constrainAs(title) {
                    start.linkTo(image.end, margin = 4.dp)
                    top.linkTo(dateText.bottom, margin = 2.dp)
                }
            )
        }
    }
}