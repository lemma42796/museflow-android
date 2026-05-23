package com.ixuea.courses.mymusic.ui.compose

import android.app.Activity
import android.widget.ImageView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.util.ImageUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MuseFlowScaffold(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    topBarContainerColor: Color = MaterialTheme.colorScheme.surface,
    topBarContentColor: Color = MaterialTheme.colorScheme.onSurface,
    actions: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("<", color = topBarContentColor)
                    }
                },
                actions = { actions() },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = topBarContainerColor,
                    titleContentColor = topBarContentColor,
                    navigationIconContentColor = topBarContentColor,
                    actionIconContentColor = topBarContentColor,
                ),
            )
        },
        bottomBar = bottomBar,
        content = content,
    )
}

@Composable
fun EmptyContent(
    text: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
fun AvatarImage(
    url: String,
    modifier: Modifier = Modifier,
    size: Dp = 50.dp,
) {
    val context = LocalContext.current
    AndroidView(
        modifier = modifier
            .size(size)
            .clip(MaterialTheme.shapes.small),
        factory = { viewContext ->
            ImageView(viewContext).apply {
                scaleType = ImageView.ScaleType.CENTER_CROP
                setImageResource(R.drawable.default_avatar)
            }
        },
        update = { imageView ->
            val activity = context as? Activity
            if (activity != null) {
                ImageUtil.showAvatar(activity, imageView, url)
            } else {
                imageView.setImageResource(R.drawable.default_avatar)
            }
        },
    )
}
