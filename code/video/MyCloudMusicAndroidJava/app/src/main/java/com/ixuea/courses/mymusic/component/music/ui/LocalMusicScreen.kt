package com.ixuea.courses.mymusic.component.music.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Unspecified
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.component.song.model.Song
import com.ixuea.courses.mymusic.ui.compose.EmptyContent
import com.ixuea.courses.mymusic.ui.compose.MuseFlowScaffold

@Composable
fun LocalMusicScreen(
    songs: List<Song>,
    editing: Boolean,
    selectedIndexes: Set<Int>,
    onBack: () -> Unit,
    onEditClick: () -> Unit,
    onScanClick: () -> Unit,
    onSortClick: () -> Unit,
    onSongClick: (Int) -> Unit,
    onDeleteOneClick: (Song) -> Unit,
    onSelectAllClick: () -> Unit,
    onDeleteSelectedClick: () -> Unit,
) {
    MuseFlowScaffold(
        title = stringResource(R.string.local_music),
        onBack = onBack,
        actions = {
            LocalMusicActions(
                editing = editing,
                onEditClick = onEditClick,
                onScanClick = onScanClick,
                onSortClick = onSortClick,
            )
        },
        bottomBar = {
            if (editing) {
                LocalMusicEditBar(
                    hasSelection = selectedIndexes.isNotEmpty(),
                    allSelected = songs.isNotEmpty() && selectedIndexes.size == songs.size,
                    onSelectAllClick = onSelectAllClick,
                    onDeleteSelectedClick = onDeleteSelectedClick,
                )
            }
        },
    ) { innerPadding ->
        if (songs.isEmpty()) {
            EmptyContent(
                text = stringResource(R.string.no_local_music),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )
            return@MuseFlowScaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(vertical = 4.dp),
        ) {
            itemsIndexed(
                items = songs,
                key = { index, song -> song.id ?: "local-song-$index" },
            ) { index, song ->
                LocalMusicRow(
                    index = index,
                    song = song,
                    editing = editing,
                    selected = index in selectedIndexes,
                    onClick = { onSongClick(index) },
                    onDeleteOneClick = { onDeleteOneClick(song) },
                )
            }
        }
    }
}

@Composable
private fun LocalMusicActions(
    editing: Boolean,
    onEditClick: () -> Unit,
    onScanClick: () -> Unit,
    onSortClick: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = onEditClick) {
        Icon(
            painter = painterResource(R.drawable.edit),
            contentDescription = stringResource(
                if (editing) R.string.cancel_edit else R.string.batch_edit,
            ),
            tint = Unspecified,
        )
    }

    IconButton(onClick = { expanded = true }) {
        Icon(
            painter = painterResource(R.drawable.more_vertical_dot),
            contentDescription = stringResource(R.string.more),
        )
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
    ) {
        DropdownMenuItem(
            text = { Text(stringResource(R.string.scan_local_music)) },
            onClick = {
                expanded = false
                onScanClick()
            },
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.sort)) },
            onClick = {
                expanded = false
                onSortClick()
            },
        )
    }
}

@Composable
private fun LocalMusicRow(
    index: Int,
    song: Song,
    editing: Boolean,
    selected: Boolean,
    onClick: () -> Unit,
    onDeleteOneClick: () -> Unit,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clickable(onClick = onClick)
                .padding(end = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier.width(50.dp),
                contentAlignment = Alignment.Center,
            ) {
                if (editing) {
                    Icon(
                        painter = painterResource(
                            if (selected) R.drawable.ic_checkbox_selected else R.drawable.ic_checkbox,
                        ),
                        contentDescription = null,
                        tint = Unspecified,
                    )
                } else {
                    Text(
                        text = (index + 1).toString(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.title.orEmpty(),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = song.singer?.nickname.orEmpty(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            if (!editing) {
                IconButton(onClick = onDeleteOneClick) {
                    Icon(
                        painter = painterResource(R.drawable.close),
                        contentDescription = stringResource(R.string.delete),
                        tint = Unspecified,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }
        }
        HorizontalDivider()
    }
}

@Composable
private fun LocalMusicEditBar(
    hasSelection: Boolean,
    allSelected: Boolean,
    onSelectAllClick: () -> Unit,
    onDeleteSelectedClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Button(
            modifier = Modifier.weight(1f),
            onClick = onSelectAllClick,
        ) {
            Text(
                text = stringResource(
                    if (allSelected) R.string.cancel_select_all else R.string.select_all,
                ),
                fontWeight = FontWeight.SemiBold,
            )
        }

        Button(
            modifier = Modifier.weight(1f),
            enabled = hasSelection,
            onClick = onDeleteSelectedClick,
        ) {
            Text(stringResource(R.string.delete))
        }
    }
}
