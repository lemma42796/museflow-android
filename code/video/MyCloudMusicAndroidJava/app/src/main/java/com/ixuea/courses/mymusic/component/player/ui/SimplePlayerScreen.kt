package com.ixuea.courses.mymusic.component.player.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.component.lyric.model.Lyric
import com.ixuea.courses.mymusic.component.lyric.view.LyricListView
import com.ixuea.courses.mymusic.component.song.model.Song
import com.ixuea.courses.mymusic.manager.MusicListManager
import com.ixuea.courses.mymusic.ui.compose.MuseFlowScaffold
import com.ixuea.courses.mymusic.util.PlayListUtil
import com.ixuea.courses.mymusic.util.SuperDateUtil

@Composable
fun SimplePlayerScreen(
    title: String,
    songs: List<Song>,
    selectedIndex: Int,
    isPlaying: Boolean,
    progress: Int,
    duration: Int,
    loopModel: Int,
    lyric: Lyric?,
    onBack: () -> Unit,
    onSongClick: (Song) -> Unit,
    onPreviousClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onLoopClick: () -> Unit,
    onSeekChange: (Int) -> Unit,
    onSeekFinished: () -> Unit,
    onLyricViewReady: (LyricListView) -> Unit,
) {
    MuseFlowScaffold(
        title = title,
        onBack = onBack,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            PlaylistPanel(
                songs = songs,
                selectedIndex = selectedIndex,
                onSongClick = onSongClick,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            )

            AndroidView(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(colorResource(R.color.black32)),
                factory = { context ->
                    LyricListView(context).apply {
                        setBackgroundColor(ContextCompat.getColor(context, R.color.black32))
                    }
                },
                update = { view ->
                    onLyricViewReady(view)
                    view.setData(lyric)
                    view.setProgress(progress)
                },
            )

            PlayerControls(
                progress = progress,
                duration = duration,
                isPlaying = isPlaying,
                loopModel = loopModel,
                onSeekChange = onSeekChange,
                onSeekFinished = onSeekFinished,
                onPreviousClick = onPreviousClick,
                onPlayPauseClick = onPlayPauseClick,
                onNextClick = onNextClick,
                onLoopClick = onLoopClick,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun PlaylistPanel(
    songs: List<Song>,
    selectedIndex: Int,
    onSongClick: (Song) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()

    LaunchedEffect(selectedIndex, songs.size) {
        if (selectedIndex >= 0 && selectedIndex < songs.size) {
            listState.animateScrollToItem(selectedIndex)
        }
    }

    if (songs.isEmpty()) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.not_play_music),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        return
    }

    LazyColumn(
        modifier = modifier,
        state = listState,
    ) {
        itemsIndexed(
            items = songs,
            key = { index, song -> song.id ?: "song-$index" },
        ) { index, song ->
            SongRow(
                song = song,
                selected = index == selectedIndex,
                onClick = { onSongClick(song) },
            )
        }
    }
}

@Composable
private fun SongRow(
    song: Song,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Text(
        text = song.title.orEmpty(),
        color = if (selected) colorResource(R.color.primary) else MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    )
}

@Composable
private fun PlayerControls(
    progress: Int,
    duration: Int,
    isPlaying: Boolean,
    loopModel: Int,
    onSeekChange: (Int) -> Unit,
    onSeekFinished: () -> Unit,
    onPreviousClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onLoopClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val boundedDuration = duration.coerceAtLeast(1)
    val boundedProgress = progress.coerceIn(0, boundedDuration)

    Column(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = SuperDateUtil.ms2ms(boundedProgress),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PlaybackIconButton(
                icon = R.drawable.music_previous,
                contentDescription = "上一曲",
                onClick = onPreviousClick,
            )
            PlaybackIconButton(
                icon = if (isPlaying) R.drawable.music_pause else R.drawable.music_play,
                contentDescription = stringResource(if (isPlaying) R.string.pause else R.string.play),
                size = 56,
                onClick = onPlayPauseClick,
            )
            PlaybackIconButton(
                icon = R.drawable.music_next,
                contentDescription = "下一曲",
                onClick = onNextClick,
            )
            OutlinedButton(onClick = onLoopClick) {
                Icon(
                    painter = painterResource(PlayListUtil.getLoopModelIcon(loopModel)),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Text(
                    text = loopModelText(loopModel),
                    modifier = Modifier.padding(start = 6.dp),
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun PlaybackIconButton(
    icon: Int,
    contentDescription: String,
    onClick: () -> Unit,
    size: Int = 48,
) {
    IconButton(onClick = onClick) {
        Icon(
            painter = painterResource(icon),
            contentDescription = contentDescription,
            tint = Color.Unspecified,
            modifier = Modifier.size(size.dp),
        )
    }
}

private fun loopModelText(loopModel: Int): String {
    return when (loopModel) {
        MusicListManager.MODEL_LOOP_RANDOM -> "随机循环"
        MusicListManager.MODEL_LOOP_ONE -> "单曲循环"
        else -> "列表循环"
    }
}
