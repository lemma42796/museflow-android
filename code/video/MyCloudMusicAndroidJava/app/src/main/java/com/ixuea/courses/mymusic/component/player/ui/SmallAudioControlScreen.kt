package com.ixuea.courses.mymusic.component.player.ui

import android.widget.ImageView
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.component.lyric.model.Line
import com.ixuea.courses.mymusic.component.lyric.model.Lyric
import com.ixuea.courses.mymusic.component.lyric.view.LyricLineView
import com.ixuea.courses.mymusic.component.song.model.Song
import com.ixuea.courses.mymusic.util.ImageUtil
import com.ixuea.courses.mymusic.util.LyricUtil
import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SmallAudioControlScreen(
    songs: List<Song>,
    selectedIndex: Int,
    isPlaying: Boolean,
    progress: Int,
    duration: Int,
    onSongSettled: (Int) -> Unit,
    onPlayPauseClick: () -> Unit,
    onListClick: () -> Unit,
    onOpenPlayerClick: () -> Unit,
) {
    if (songs.isEmpty()) {
        return
    }

    val pagerState = rememberPagerState(pageCount = { songs.size })
    val boundedSelectedIndex = selectedIndex.coerceIn(0, songs.lastIndex)

    LaunchedEffect(boundedSelectedIndex, songs.size) {
        if (pagerState.currentPage != boundedSelectedIndex) {
            pagerState.scrollToPage(boundedSelectedIndex)
        }
    }

    LaunchedEffect(pagerState, songs) {
        snapshotFlow { pagerState.settledPage }
            .distinctUntilChanged()
            .collect { page ->
                if (page in songs.indices && page != selectedIndex) {
                    onSongSettled(page)
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface),
    ) {
        HorizontalDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(start = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp),
            ) { page ->
                SmallAudioSongPage(
                    song = songs[page],
                    progress = progress,
                    active = page == boundedSelectedIndex,
                    onOpenPlayerClick = onOpenPlayerClick,
                )
            }
            SmallControlButton(
                icon = if (isPlaying) R.drawable.music_pause else R.drawable.music_play,
                contentDescription = if (isPlaying) "暂停" else "播放",
                onClick = onPlayPauseClick,
            )
            SmallControlButton(
                icon = R.drawable.music_list,
                contentDescription = "播放列表",
                onClick = onListClick,
            )
        }
        val progressFraction = if (duration > 0) {
            (progress.toFloat() / duration).coerceIn(0f, 1f)
        } else {
            0f
        }
        LinearProgressIndicator(
            progress = { progressFraction },
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

@Composable
private fun SmallAudioSongPage(
    song: Song,
    progress: Int,
    active: Boolean,
    onOpenPlayerClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .clickable(onClick = onOpenPlayerClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SongCover(song = song)
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 5.dp),
        ) {
            Text(
                text = song.title.orEmpty(),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            SmallLyricLine(
                song = song,
                progress = progress,
                active = active,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp),
            )
        }
    }
}

@Composable
private fun SongCover(song: Song) {
    val context = LocalContext.current
    AndroidView(
        modifier = Modifier.size(35.dp),
        factory = { viewContext ->
            ImageView(viewContext).apply {
                scaleType = ImageView.ScaleType.CENTER_CROP
                setImageResource(R.drawable.placeholder)
            }
        },
        update = { imageView ->
            ImageUtil.show(context, imageView, song.icon)
        },
    )
}

@Composable
private fun SmallLyricLine(
    song: Song,
    progress: Int,
    active: Boolean,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        modifier = modifier,
        factory = { viewContext ->
            LyricLineView(viewContext).apply {
                setLineSelected(true)
                setLyricTextSize(resources.getDimensionPixelSize(R.dimen.text_small))
                setLyricTextColor(ContextCompat.getColor(viewContext, R.color.black80))
                setLyricSelectedTextColor(ContextCompat.getColor(viewContext, R.color.primary))
            }
        },
        update = { lyricLineView ->
            val lyric = song.parsedLyric
            val line = resolveLine(lyric, if (active) progress else song.progress.toInt())
            lyricLineView.setData(line)
            lyricLineView.setAccurate(lyric?.isAccurate == true)

            if (line != null && lyric?.isAccurate == true) {
                val lineProgress = if (active) progress.toLong() else song.progress
                lyricLineView.setLyricCurrentWordIndex(LyricUtil.getWordIndex(line, lineProgress))
                lyricLineView.setWordPlayedTime(LyricUtil.getWordPlayedTime(line, lineProgress))
            }
            lyricLineView.onProgress()
        },
    )
}

@Composable
private fun SmallControlButton(
    icon: Int,
    contentDescription: String,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.width(50.dp),
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = contentDescription,
            tint = colorResource(R.color.primary),
            modifier = Modifier.size(26.dp),
        )
    }
}

private fun resolveLine(lyric: Lyric?, progress: Int): Line? {
    val lines = lyric?.datum
    if (lines.isNullOrEmpty()) {
        return null
    }

    val lineNumber = LyricUtil.getLineNumber(lyric, progress).coerceIn(lines.indices)
    return lines[lineNumber]
}
