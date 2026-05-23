package com.ixuea.courses.mymusic.component.player.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.component.song.model.Song
import com.ixuea.courses.mymusic.manager.MusicListManager
import com.ixuea.courses.mymusic.util.PlayListUtil

@Composable
fun MusicPlayListSheet(
    songs: List<Song>,
    currentSongId: String?,
    loopModel: Int,
    onLoopClick: () -> Unit,
    onDeleteAllClick: () -> Unit,
    onSongClick: (Song) -> Unit,
    onDeleteSongClick: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
    ) {
        PlayListHeader(
            loopModel = loopModel,
            count = songs.size,
            onLoopClick = onLoopClick,
            onDeleteAllClick = onDeleteAllClick,
        )
        HorizontalDivider()
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 460.dp),
        ) {
            itemsIndexed(
                items = songs,
                key = { index, song -> song.id ?: "playlist-$index" },
            ) { index, song ->
                PlayListSongRow(
                    song = song,
                    selected = song.id == currentSongId,
                    onClick = { onSongClick(song) },
                    onDeleteClick = { onDeleteSongClick(index) },
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun PlayListHeader(
    loopModel: Int,
    count: Int,
    onLoopClick: () -> Unit,
    onDeleteAllClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 55.dp)
            .padding(start = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(R.drawable.play),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(20.dp),
        )
        Row(
            modifier = Modifier
                .clickable(onClick = onLoopClick)
                .padding(horizontal = 10.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                painter = painterResource(PlayListUtil.getLoopModelIcon(loopModel)),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = loopModelText(loopModel),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Text(
            text = "($count)",
            color = colorResource(R.color.black80),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )
        IconButton(onClick = onDeleteAllClick) {
            Icon(
                painter = painterResource(R.drawable.close),
                contentDescription = "删除全部",
                tint = Color.Unspecified,
            )
        }
    }
}

@Composable
private fun PlayListSongRow(
    song: Song,
    selected: Boolean,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 55.dp)
            .clickable(onClick = onClick)
            .padding(start = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = song.displayTitle(),
            color = if (selected) colorResource(R.color.primary) else colorResource(R.color.black32),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        IconButton(onClick = onDeleteClick) {
            Icon(
                painter = painterResource(R.drawable.close),
                contentDescription = "删除",
                tint = Color.Unspecified,
            )
        }
    }
}

private fun Song.displayTitle(): String {
    val title = title.orEmpty()
    val singer = singer?.nickname.orEmpty()
    return if (singer.isBlank()) title else "$title - $singer"
}

private fun loopModelText(loopModel: Int): String {
    return when (loopModel) {
        MusicListManager.MODEL_LOOP_RANDOM -> "随机循环"
        MusicListManager.MODEL_LOOP_ONE -> "单曲循环"
        else -> "列表循环"
    }
}
