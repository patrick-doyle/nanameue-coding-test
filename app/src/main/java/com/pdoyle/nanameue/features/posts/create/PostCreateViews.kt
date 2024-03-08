package com.pdoyle.nanameue.features.posts.create

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.pdoyle.nanameue.R
import com.pdoyle.nanameue.app.posts.PostForm
import com.pdoyle.nanameue.util.emptyString

@Composable
fun PostForm(
    innerPadding: PaddingValues = PaddingValues(0.dp),
    onPostSubmit: (PostForm) -> Unit = {},
) {

    var postForm: PostForm by remember {
        mutableStateOf(PostForm(emptyString(), emptyString()))
    }

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            postForm = postForm.copy(imageUrl = uri.toString())
        }

    val painter = rememberAsyncImagePainter(
        ImageRequest
            .Builder(LocalContext.current)
            .data(data = postForm.imageUrl)
            .build()
    )

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .verticalScroll(rememberScrollState())
    ) {

        if (postForm.imageUrl != null) {
            Image(
                painter = painter,
                contentScale = ContentScale.Crop,
                contentDescription = "Selected Image",
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(200.dp)
            )
        }

        PostField(
            label = stringResource(R.string.create_post),
            value = postForm.text ?: emptyString(),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            onChange = { postForm = postForm.copy(text = it) },
            submit = {
                onPostSubmit(postForm)
            }
        )
        TextButton(
            onClick = {
                launcher.launch(
                    PickVisualMediaRequest(
                        mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(horizontal = 8.dp, vertical = 4.dp),
        ) {
            Text(
                text = stringResource(R.string.select_image)
            )
        }
        TextButton(
            onClick = {
                onPostSubmit(postForm)
            },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(horizontal = 8.dp, vertical = 4.dp),
        ) {
            Text(
                text = stringResource(R.string.create_post)
            )
        }
    }
}

@Preview
@Composable
fun PostField(
    value: String = "",
    label: String = "Post Text",
    modifier: Modifier = Modifier.fillMaxWidth(),
    onChange: (String) -> Unit = {},
    submit: () -> Unit = {}
) {

    TextField(
        value = value,
        onValueChange = onChange,
        modifier = modifier,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Text
        ),
        keyboardActions = KeyboardActions(
            onDone = { submit() }
        ),
        label = { Text(label) },
        minLines = 10
    )
}