package com.ixuea.courses.mymusic.component.chat.ui

import android.widget.ImageView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Unspecified
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.component.chat.model.MediaMessageExtra
import com.ixuea.courses.mymusic.ui.compose.AvatarImage
import com.ixuea.courses.mymusic.ui.compose.EmptyContent
import com.ixuea.courses.mymusic.ui.compose.MuseFlowScaffold
import com.ixuea.courses.mymusic.util.ImageUtil
import com.ixuea.courses.mymusic.util.JSONUtil
import io.rong.imlib.model.Message
import io.rong.message.ImageMessage
import io.rong.message.TextMessage

@Composable
fun ChatScreen(
    state: ChatUiState,
    inputText: String,
    onInputChange: (String) -> Unit,
    onBack: () -> Unit,
    onLoadMore: () -> Unit,
    onSelectImage: () -> Unit,
    onSendText: () -> Unit,
) {
    val listState = rememberLazyListState()

    LaunchedEffect(state.scrollToBottomVersion, state.messages.size) {
        if (state.scrollToBottomVersion > 0 && state.messages.isNotEmpty()) {
            listState.scrollToItem(state.messages.lastIndex)
        }
    }

    LaunchedEffect(state.smoothScrollBottomVersion, state.messages.size) {
        if (state.smoothScrollBottomVersion > 0 && state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.lastIndex)
        }
    }

    MuseFlowScaffold(
        title = state.targetTitle,
        onBack = onBack,
        bottomBar = {
            ChatInputBar(
                inputText = inputText,
                isSending = state.sendOperation != ChatSendOperation.NONE,
                onInputChange = onInputChange,
                onSelectImage = onSelectImage,
                onSendText = onSendText,
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            if (state.messages.isEmpty() && !state.isLoadingHistory) {
                EmptyContent(
                    text = stringResource(R.string.no_message),
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    item {
                        TextButton(
                            enabled = !state.isLoadingHistory,
                            onClick = onLoadMore,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(stringResource(R.string.load_more))
                        }
                    }

                    items(
                        count = state.messages.size,
                        key = { index ->
                            state.messages[index].message.messageId.takeIf { it != 0 }
                                ?: "message-$index"
                        },
                    ) { index ->
                        ChatMessageRow(state.messages[index])
                    }
                }
            }

            if (state.isLoadingHistory) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun ChatMessageRow(item: ChatMessageUiState) {
    val message = item.message
    val isSend = message.messageDirection == Message.MessageDirection.SEND

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = if (isSend) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top,
    ) {
        if (!isSend) {
            AvatarImage(url = item.senderIcon, size = 40.dp)
            Spacer(modifier = Modifier.width(5.dp))
        }

        when (val content = message.content) {
            is ImageMessage -> ChatImageBubble(
                imageMessage = content,
            )

            is TextMessage -> ChatTextBubble(
                text = content.content.orEmpty(),
                isSend = isSend,
            )

            else -> ChatTextBubble(
                text = content?.javaClass?.simpleName.orEmpty(),
                isSend = isSend,
            )
        }

        if (isSend) {
            Spacer(modifier = Modifier.width(5.dp))
            AvatarImage(url = item.senderIcon, size = 40.dp)
        }
    }
}

@Composable
private fun ChatTextBubble(
    text: String,
    isSend: Boolean,
) {
    val bubbleColor = if (isSend) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val contentColor = if (isSend) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Surface(
        color = bubbleColor,
        contentColor = contentColor,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .heightIn(min = 40.dp),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun ChatImageBubble(
    imageMessage: ImageMessage,
) {
    val extra = parseMediaMessageExtra(imageMessage.extra)
    val (width, height) = chatImageSize(extra?.width ?: 0, extra?.height ?: 0)

    Surface(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.padding(horizontal = 4.dp),
    ) {
        ChatRemoteImage(
            imageMessage = imageMessage,
            modifier = Modifier
                .size(width = width, height = height)
                .clip(RoundedCornerShape(8.dp)),
        )
    }
}

private fun parseMediaMessageExtra(extra: String?): MediaMessageExtra? {
    if (extra.isNullOrBlank()) {
        return null
    }

    return runCatching {
        JSONUtil.fromJSON(extra, MediaMessageExtra::class.java)
    }.getOrNull()
}

private fun chatImageSize(width: Int, height: Int): Pair<Dp, Dp> {
    val maxSize = 150.dp
    if (width <= 0 || height <= 0) {
        return maxSize to maxSize
    }

    return if (width > height) {
        maxSize to (150f * height / width).dp
    } else {
        (150f * width / height).dp to maxSize
    }
}

@Composable
private fun ChatRemoteImage(
    imageMessage: ImageMessage,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val uri = imageMessage.remoteUri?.toString()
        ?: imageMessage.localUri?.toString()
        ?: imageMessage.thumUri?.toString()
        ?: ""
    val isLocal = imageMessage.remoteUri == null && imageMessage.localUri != null

    AndroidView(
        modifier = modifier,
        factory = { viewContext ->
            ImageView(viewContext).apply {
                scaleType = ImageView.ScaleType.CENTER_CROP
                setImageResource(R.drawable.placeholder)
            }
        },
        update = { imageView ->
            if (imageView.tag == uri) {
                return@AndroidView
            }

            imageView.tag = uri
            when {
                uri.isBlank() -> imageView.setImageDrawable(null)
                isLocal -> ImageUtil.showLocalImage(context, imageView, uri)
                else -> ImageUtil.showFull(context, imageView, uri)
            }
        },
    )
}

@Composable
private fun ChatInputBar(
    inputText: String,
    isSending: Boolean,
    onInputChange: (String) -> Unit,
    onSelectImage: () -> Unit,
    onSendText: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            enabled = !isSending,
            onClick = onSelectImage,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_select_image),
                contentDescription = stringResource(R.string.select_image),
                tint = Unspecified,
            )
        }

        OutlinedTextField(
            value = inputText,
            onValueChange = onInputChange,
            modifier = Modifier.weight(1f),
            enabled = !isSending,
            placeholder = {
                Text(stringResource(R.string.hint_enter_message))
            },
            maxLines = 4,
        )

        IconButton(
            enabled = !isSending,
            onClick = onSendText,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_send),
                contentDescription = stringResource(R.string.send),
                tint = Unspecified,
            )
        }
    }
}
