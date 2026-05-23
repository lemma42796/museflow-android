package com.ixuea.courses.mymusic.component.feed.ui

import android.text.SpannableStringBuilder
import android.widget.ImageView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Unspecified
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.emoji.widget.EmojiTextView
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.component.comment.model.Comment
import com.ixuea.courses.mymusic.component.feed.model.Feed
import com.ixuea.courses.mymusic.component.user.model.User
import com.ixuea.courses.mymusic.model.Resource
import com.ixuea.courses.mymusic.ui.compose.AvatarImage
import com.ixuea.courses.mymusic.ui.compose.EmptyContent
import com.ixuea.courses.mymusic.util.Constant
import com.ixuea.courses.mymusic.util.ImageUtil
import com.ixuea.courses.mymusic.util.SpannableStringBuilderUtil
import com.ixuea.courses.mymusic.util.SuperDateUtil
import com.ixuea.courses.mymusic.util.SuperTextUtil

@Composable
fun FeedScreen(
    state: FeedUiState,
    isLogin: Boolean,
    currentUserId: String?,
    onCreateClick: () -> Unit,
    onImageClick: (List<String>, Int) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (state.feeds.isEmpty() && !state.isLoading) {
            EmptyContent(
                text = stringResource(R.string.no_feed),
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 8.dp, bottom = 92.dp),
            ) {
                items(
                    items = state.feeds,
                    key = { feed -> feed.id ?: feed.createdAt ?: feed.hashCode().toString() },
                ) { feed ->
                    FeedItem(
                        feed = feed,
                        isOwner = isLogin && feed.user?.id == currentUserId,
                        currentUserId = currentUserId,
                        onImageClick = onImageClick,
                    )
                    HorizontalDivider()
                }
            }
        }

        if (state.isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        FloatingActionButton(
            onClick = onCreateClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 60.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.add),
                contentDescription = stringResource(R.string.activity_publish_feed),
                tint = Unspecified,
            )
        }
    }
}

@Composable
private fun FeedItem(
    feed: Feed,
    isOwner: Boolean,
    currentUserId: String?,
    onImageClick: (List<String>, Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        AvatarImage(
            url = feed.user?.icon.orEmpty(),
            size = 44.dp,
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = feed.user?.nickname.orEmpty(),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            FeedContentText(
                text = feed.content.orEmpty(),
                modifier = Modifier.padding(top = 5.dp),
            )

            FeedMediaGrid(
                medias = feed.medias.orEmpty(),
                onImageClick = onImageClick,
            )

            if (!feed.province.isNullOrBlank()) {
                Text(
                    text = "%s . %s".format(feed.city.orEmpty(), feed.position.orEmpty()),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }

            FeedActionRow(
                feed = feed,
                isOwner = isOwner,
                currentUserId = currentUserId,
            )

            FeedSocialBox(feed)
        }
    }
}

@Composable
private fun FeedContentText(
    text: String,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { context ->
            EmojiTextView(context).apply {
                setTextColor(context.getColor(R.color.black))
                textSize = 17f
                setLineSpacing(0f, 1.2f)
            }
        },
        update = { textView ->
            textView.text = text
        },
    )
}

@Composable
private fun FeedMediaGrid(
    medias: List<Resource>,
    onImageClick: (List<String>, Int) -> Unit,
) {
    if (medias.isEmpty()) {
        return
    }

    val spanCount = when {
        medias.size > 4 -> 3
        medias.size > 1 -> 2
        else -> 1
    }
    val urls = medias.map { it.uri.orEmpty() }

    Column(
        modifier = Modifier.padding(top = 10.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        medias.chunked(spanCount).forEachIndexed { rowIndex, row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                row.forEachIndexed { columnIndex, media ->
                    val index = rowIndex * spanCount + columnIndex
                    RemoteFeedImage(
                        url = media.uri,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                onImageClick(urls, index)
                            },
                    )
                }
                repeat(spanCount - row.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun FeedActionRow(
    feed: Feed,
    isOwner: Boolean,
    currentUserId: String?,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = SuperDateUtil.commonFormat(feed.createdAt),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall,
        )

        if (isOwner) {
            Text(
                text = stringResource(R.string.delete),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 10.dp),
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        val liked = currentUserId != null && feed.likes.orEmpty().contains(User(currentUserId))
        IconButton(onClick = {}) {
            Icon(
                painter = painterResource(if (liked) R.drawable.thumb_selected else R.drawable.thumb),
                contentDescription = stringResource(R.string.like),
                tint = Unspecified,
                modifier = Modifier.size(18.dp),
            )
        }

        IconButton(onClick = {}) {
            Icon(
                painter = painterResource(R.drawable.comment_count_small),
                contentDescription = stringResource(R.string.comment),
                tint = Unspecified,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

@Composable
private fun FeedSocialBox(feed: Feed) {
    val likes = feed.likes.orEmpty()
    val comments = feed.comments.orEmpty()
    val replyText = stringResource(R.string.reply)
    val colonSeparator = stringResource(R.string.colon_separator)
    if (likes.isEmpty() && comments.isEmpty()) {
        return
    }

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
        shape = RoundedCornerShape(5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)) {
            if (likes.isNotEmpty()) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        painter = painterResource(R.drawable.heart_solid),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .size(18.dp),
                    )
                    FeedSpannableText(
                        text = processLikeUserContent(likes),
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .weight(1f),
                    )
                }
            }

            if (comments.isNotEmpty()) {
                if (likes.isNotEmpty()) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 5.dp))
                }
                comments.forEach { comment ->
                    FeedSpannableText(
                        text = processCommentContent(comment, replyText, colonSeparator),
                        modifier = Modifier.padding(vertical = 3.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun FeedSpannableText(
    text: SpannableStringBuilder,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { viewContext ->
            EmojiTextView(viewContext).apply {
                textSize = 14f
                setTextColor(viewContext.getColor(R.color.black80))
                SuperTextUtil.setLinkColor(this, viewContext.getColor(R.color.link))
            }
        },
        update = { textView ->
            SuperTextUtil.setLinkColor(textView, context.getColor(R.color.link))
            textView.text = text
        },
    )
}

private fun processCommentContent(
    data: Comment,
    replyText: String,
    colonSeparator: String,
): SpannableStringBuilder {
    val result = SpannableStringBuilder()

    val user = data.user
    result.append(user?.nickname.orEmpty())
    SpannableStringBuilderUtil.setUserClickSpan(result, 0, result.length, user?.id)

    data.parent?.let { parent ->
        result.append(replyText)

        val start = result.length
        result.append(parent.user?.nickname.orEmpty())
        SpannableStringBuilderUtil.setUserClickSpan(result, start, result.length, parent.user?.id)
    }

    result.append(colonSeparator)
    result.append(data.content.orEmpty())

    return result
}

private fun processLikeUserContent(data: List<User>): SpannableStringBuilder {
    val result = SpannableStringBuilder()
    var start = 0

    data.forEachIndexed { index, user ->
        result.append(user.nickname)
        SpannableStringBuilderUtil.setUserClickSpan(result, start, result.length, user.id)

        if (index != data.size - 1) {
            result.append(Constant.SEPARATOR)
        }

        start = result.length
    }

    return result
}

@Composable
private fun RemoteFeedImage(
    url: String?,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    AndroidView(
        modifier = modifier,
        factory = { viewContext ->
            ImageView(viewContext).apply {
                scaleType = ImageView.ScaleType.CENTER_CROP
                setImageResource(R.drawable.placeholder)
            }
        },
        update = { imageView ->
            val value = url.orEmpty()
            if (imageView.tag != value) {
                imageView.tag = value
                ImageUtil.show(context, imageView, value)
            }
        },
    )
}
