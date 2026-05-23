package com.ixuea.courses.mymusic.component.comment.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.emoji.widget.EmojiAppCompatTextView
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.component.comment.model.Comment
import com.ixuea.courses.mymusic.component.user.activity.UserDetailActivity
import com.ixuea.courses.mymusic.ui.compose.AvatarImage
import com.ixuea.courses.mymusic.ui.compose.EmptyContent
import com.ixuea.courses.mymusic.ui.compose.MuseFlowScaffold
import com.ixuea.courses.mymusic.util.RichUtil
import com.ixuea.courses.mymusic.util.SuperDateUtil
import com.ixuea.courses.mymusic.util.SuperTextUtil

@Composable
fun CommentScreen(
    state: CommentUiState,
    input: String,
    replyHintName: String?,
    onInputChange: (String) -> Unit,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onSubmit: () -> Unit,
    onLike: (Comment) -> Unit,
    onUserClick: (Comment) -> Unit,
    onCommentMore: (Comment) -> Unit,
) {
    MuseFlowScaffold(
        title = stringResource(R.string.activity_comment),
        onBack = onBack,
        bottomBar = {
            CommentInputBar(
                input = input,
                replyHintName = replyHintName,
                isSubmitting = state.isSubmitting,
                onInputChange = onInputChange,
                onSubmit = onSubmit,
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.comment_count2, state.comments.size),
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
                TextButton(onClick = onRefresh, enabled = !state.isLoading) {
                    Text(stringResource(R.string.refresh))
                }
            }

            if (state.comments.isEmpty() && !state.isLoading) {
                EmptyContent(
                    text = stringResource(R.string.no_comment),
                    modifier = Modifier.weight(1f),
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 12.dp),
                ) {
                    items(
                        items = state.comments,
                        key = { it.id ?: it.hashCode().toString() },
                    ) { comment ->
                        CommentRow(
                            comment = comment,
                            onUserClick = { onUserClick(comment) },
                            onLike = { onLike(comment) },
                            onCommentMore = { onCommentMore(comment) },
                        )
                        HorizontalDivider()
                    }

                    item {
                        LoadMoreRow(
                            state = state,
                            onLoadMore = onLoadMore,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CommentRow(
    comment: Comment,
    onUserClick: () -> Unit,
    onLike: () -> Unit,
    onCommentMore: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AvatarImage(
                url = comment.user?.icon.orEmpty(),
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(onClick = onUserClick),
                size = 42.dp,
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onUserClick),
            ) {
                Text(
                    text = comment.user?.nickname.orEmpty(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = SuperDateUtil.commonFormat(comment.createdAt),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            TextButton(onClick = onLike) {
                Text(
                    text = "${comment.likesCount} ${stringResource(R.string.like)}",
                    color = if (comment.isLiked) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                )
            }
        }

        RichCommentText(
            content = comment.content.orEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 52.dp, top = 10.dp, end = 8.dp),
        )

        val parent = comment.parent
        if (parent != null) {
            RichCommentText(
                content = stringResource(
                    R.string.reply_comment,
                    parent.user?.nickname.orEmpty(),
                    parent.content.orEmpty(),
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 52.dp, top = 10.dp, end = 8.dp),
                muted = true,
            )
        }

        TextButton(
            modifier = Modifier.padding(start = 40.dp),
            onClick = onCommentMore,
        ) {
            Text(stringResource(R.string.more))
        }
    }
}

@Composable
private fun RichCommentText(
    content: String,
    modifier: Modifier = Modifier,
    muted: Boolean = false,
) {
    val context = LocalContext.current
    AndroidView(
        modifier = modifier,
        factory = { viewContext ->
            EmojiAppCompatTextView(viewContext).apply {
                setLineSpacing(0f, 1.3f)
                SuperTextUtil.setLinkColor(this, ContextCompat.getColor(viewContext, R.color.link))
            }
        },
        update = { textView ->
            textView.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (muted) R.color.black80 else R.color.black,
                ),
            )
            textView.text = RichUtil.processContent(
                content,
                { data, _ ->
                    val clickText = RichUtil.removePlaceholderString(data)
                    UserDetailActivity.startWithNickname(context, clickText)
                },
                { _, _ -> },
            )
        },
    )
}

@Composable
private fun LoadMoreRow(
    state: CommentUiState,
    onLoadMore: () -> Unit,
) {
    val text = when {
        state.isLoading -> stringResource(R.string.loading)
        state.noMoreData -> stringResource(R.string.no_more_data)
        else -> stringResource(R.string.load_more)
    }
    BoxTextButton(
        text = text,
        enabled = !state.isLoading && !state.noMoreData,
        onClick = onLoadMore,
    )
}

@Composable
private fun BoxTextButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextButton(
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            onClick = onClick,
        ) {
            Text(text)
        }
    }
}

@Composable
private fun CommentInputBar(
    input: String,
    replyHintName: String?,
    isSubmitting: Boolean,
    onInputChange: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .imePadding()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedTextField(
            value = input,
            onValueChange = { onInputChange(it.take(MAX_COMMENT_LENGTH)) },
            modifier = Modifier.weight(1f),
            placeholder = {
                Text(
                    text = replyHintName?.let {
                        stringResource(R.string.reply_hint, it)
                    } ?: stringResource(R.string.hint_comment),
                )
            },
            maxLines = 3,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            enabled = !isSubmitting,
            onClick = onSubmit,
        ) {
            Text(stringResource(R.string.send))
        }
    }
}

private const val MAX_COMMENT_LENGTH = 140
