package com.ixuea.courses.mymusic.component.player.ui

import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.component.lyric.view.LyricListView
import com.ixuea.courses.mymusic.component.player.view.RecordPageView
import com.ixuea.courses.mymusic.util.PlayListUtil
import com.ixuea.courses.mymusic.util.SuperDateUtil

@Composable
fun MusicPlayerScreen(
    title: String,
    subtitle: String,
    isPlaying: Boolean,
    isLyricVisible: Boolean,
    progress: Int,
    duration: Int,
    loopModel: Int,
    downloadIcon: Int,
    onBack: () -> Unit,
    onDownloadClick: () -> Unit,
    onLoopClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onListClick: () -> Unit,
    onSeekChange: (Int) -> Unit,
    onSeekFinished: () -> Unit,
    onBackgroundReady: (ImageView) -> Unit,
    onRecordReady: (RecordPageView) -> Unit,
    onLyricReady: (LyricListView) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                ImageView(context).apply {
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    setImageResource(R.drawable.default_cover)
                }
            },
            update = onBackgroundReady,
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f)),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
        ) {
            MusicPlayerTopBar(
                title = title,
                subtitle = subtitle,
                onBack = onBack,
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                AndroidView(
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(if (isLyricVisible) 0f else 1f),
                    factory = { context -> RecordPageView(context) },
                    update = { view ->
                        onRecordReady(view)
                        view.setPlaying(isPlaying && !isLyricVisible)
                    },
                )
                if (isLyricVisible) {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { context -> LyricListView(context) },
                        update = { view ->
                            onLyricReady(view)
                            view.setProgress(progress)
                        },
                    )
                }
            }

            MiniActionBar(
                downloadIcon = downloadIcon,
                onDownloadClick = onDownloadClick,
            )
            ProgressBar(
                progress = progress,
                duration = duration,
                onSeekChange = onSeekChange,
                onSeekFinished = onSeekFinished,
            )
            PlayerActionBar(
                isPlaying = isPlaying,
                loopModel = loopModel,
                onLoopClick = onLoopClick,
                onPreviousClick = onPreviousClick,
                onPlayPauseClick = onPlayPauseClick,
                onNextClick = onNextClick,
                onListClick = onListClick,
                modifier = Modifier.padding(bottom = 20.dp),
            )
        }
    }
}

@Composable
private fun MusicPlayerTopBar(
    title: String,
    subtitle: String,
    onBack: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Text("<", color = Color.White)
        }
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (subtitle.isNotBlank()) {
                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.76f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Spacer(modifier = Modifier.size(48.dp))
    }
}

@Composable
private fun MiniActionBar(
    downloadIcon: Int,
    onDownloadClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PlayerIcon(R.drawable.ic_like, "喜欢", onClick = {})
        PlayerIcon(downloadIcon, "下载", onClick = onDownloadClick)
        PlayerIcon(R.drawable.ic_eq, "均衡器", onClick = {})
        PlayerIcon(R.drawable.ic_comment_count, "评论", onClick = {})
        PlayerIcon(R.drawable.ellipsis_vertical, "更多", tint = Color.White, onClick = {})
    }
}

@Composable
private fun ProgressBar(
    progress: Int,
    duration: Int,
    onSeekChange: (Int) -> Unit,
    onSeekFinished: () -> Unit,
) {
    val boundedDuration = duration.coerceAtLeast(1)
    val boundedProgress = progress.coerceIn(0, boundedDuration)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 25.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = SuperDateUtil.ms2ms(boundedProgress),
            color = Color.White,
        )
        Slider(
            value = boundedProgress.toFloat(),
            onValueChange = { onSeekChange(it.toInt()) },
            onValueChangeFinished = onSeekFinished,
            valueRange = 0f..boundedDuration.toFloat(),
            enabled = duration > 0,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
        )
        Text(
            text = SuperDateUtil.ms2ms(duration.coerceAtLeast(0)),
            color = Color.White,
        )
    }
}

@Composable
private fun PlayerActionBar(
    isPlaying: Boolean,
    loopModel: Int,
    onLoopClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onListClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PlayerIcon(
            icon = PlayListUtil.getLoopModelIcon(loopModel),
            contentDescription = "循环模式",
            size = 54,
            onClick = onLoopClick,
        )
        PlayerIcon(R.drawable.music_previous, "上一曲", size = 54, onClick = onPreviousClick)
        PlayerIcon(
            icon = if (isPlaying) R.drawable.music_pause else R.drawable.music_play,
            contentDescription = if (isPlaying) "暂停" else "播放",
            size = 66,
            onClick = onPlayPauseClick,
        )
        PlayerIcon(R.drawable.music_next, "下一曲", size = 54, onClick = onNextClick)
        PlayerIcon(R.drawable.music_list, "播放列表", size = 54, onClick = onListClick)
    }
}

@Composable
private fun PlayerIcon(
    icon: Int,
    contentDescription: String,
    onClick: () -> Unit,
    size: Int = 40,
    tint: Color = Color.Unspecified,
) {
    IconButton(onClick = onClick) {
        Icon(
            painter = painterResource(icon),
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(size.dp),
        )
    }
}
