package com.ixuea.courses.mymusic.component.feed.ui

import android.widget.ImageView
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.model.Resource
import com.ixuea.courses.mymusic.ui.compose.MuseFlowScaffold
import com.ixuea.courses.mymusic.util.ImageUtil
import com.luck.picture.lib.entity.LocalMedia

@Composable
fun FeedPublishScreen(
    state: FeedPublishUiState,
    content: String,
    onContentChange: (String) -> Unit,
    onSelectImage: () -> Unit,
    onRemoveImage: (Int) -> Unit,
    onPublish: (String) -> Unit,
    onBack: () -> Unit,
) {
    val canPublish = state.operation == FeedPublishOperation.NONE

    MuseFlowScaffold(
        title = stringResource(R.string.activity_publish_feed),
        onBack = onBack,
        actions = {
            TextButton(
                enabled = canPublish,
                onClick = { onPublish(content) },
            ) {
                Text(stringResource(R.string.publish))
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            OutlinedTextField(
                value = content,
                onValueChange = { next ->
                    onContentChange(next.take(MAX_CONTENT_LENGTH))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                placeholder = { Text(stringResource(R.string.hint_feed)) },
                supportingText = {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = stringResource(R.string.feed_count, content.length),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End,
                        )
                    }
                },
                maxLines = 8,
            )

            FeedMediaGrid(
                items = state.mediaItems,
                onSelectImage = onSelectImage,
                onRemoveImage = onRemoveImage,
            )

            if (state.operation != FeedPublishOperation.NONE) {
                Text(
                    text = if (state.operation == FeedPublishOperation.UPLOADING_IMAGES) {
                        stringResource(R.string.loading_upload, 1)
                    } else {
                        stringResource(R.string.loading)
                    },
                    modifier = Modifier.padding(top = 12.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Composable
private fun FeedMediaGrid(
    items: List<Any>,
    onSelectImage: () -> Unit,
    onRemoveImage: (Int) -> Unit,
) {
    val rows = ((items.size + GRID_COLUMNS - 1) / GRID_COLUMNS).coerceAtLeast(1)
    LazyVerticalGrid(
        columns = GridCells.Fixed(GRID_COLUMNS),
        modifier = Modifier
            .fillMaxWidth()
            .height((rows * MEDIA_TILE_SIZE_DP).dp)
            .padding(top = 16.dp),
        contentPadding = PaddingValues(2.dp),
    ) {
        itemsIndexed(items) { index, item ->
            FeedMediaTile(
                item = item,
                onClick = {
                    if (item is Int) {
                        onSelectImage()
                    }
                },
                onRemoveClick = {
                    onRemoveImage(index)
                },
            )
        }
    }
}

@Composable
private fun FeedMediaTile(
    item: Any,
    onClick: () -> Unit,
    onRemoveClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .aspectRatio(1f)
            .clip(MaterialTheme.shapes.small)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        when (item) {
            is LocalMedia,
            is Resource,
            is Int -> FeedMediaImage(
                item = item,
                modifier = Modifier.fillMaxSize(),
            )
        }

        if (item is LocalMedia) {
            IconButton(
                modifier = Modifier.align(Alignment.TopEnd),
                onClick = onRemoveClick,
            ) {
                Image(
                    painter = painterResource(R.drawable.close_circle),
                    contentDescription = null,
                )
            }
        }
    }
}

@Composable
private fun FeedMediaImage(
    item: Any,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    AndroidView(
        modifier = modifier,
        factory = { viewContext ->
            ImageView(viewContext).apply {
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
        },
        update = { imageView ->
            when (item) {
                is Resource -> ImageUtil.show(context, imageView, item.uri)
                is LocalMedia -> ImageUtil.showLocalImage(context, imageView, item.compressPath)
                is Int -> imageView.setImageResource(item)
            }
        },
    )
}

private const val GRID_COLUMNS = 4
private const val MEDIA_TILE_SIZE_DP = 82
private const val MAX_CONTENT_LENGTH = 140
