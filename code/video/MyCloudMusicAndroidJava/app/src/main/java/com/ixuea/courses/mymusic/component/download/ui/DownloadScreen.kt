package com.ixuea.courses.mymusic.component.download.ui

import android.os.Trace
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ixuea.android.downloader.domain.DownloadInfo
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.component.download.listener.MyDownloadListener
import com.ixuea.courses.mymusic.component.song.model.Song
import com.ixuea.courses.mymusic.ui.compose.EmptyContent
import com.ixuea.courses.mymusic.ui.compose.MuseFlowScaffold
import com.ixuea.courses.mymusic.util.FileUtil

const val DOWNLOAD_INITIAL_TAB_EXTRA = "com.ixuea.courses.mymusic.DOWNLOAD_INITIAL_TAB"

private const val DOWNLOAD_SCREEN_MARKER = "MuseFlowDownloadScreen"
private const val DOWNLOADED_LIST_MARKER = "MuseFlowDownloadedList"
private const val DOWNLOADING_LIST_MARKER = "MuseFlowDownloadingList"

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DownloadScreen(
    downloadedState: DownloadedUiState,
    downloadingState: DownloadingUiState,
    songTitleForDownload: (DownloadInfo) -> String,
    onBack: () -> Unit,
    onDownloadedSongClick: (Song) -> Unit,
    onToggleDownload: (DownloadInfo) -> Unit,
    onDeleteDownload: (DownloadInfo) -> Unit,
    onDownloadTerminalState: (DownloadInfo) -> Unit,
    onToggleAllDownloads: () -> Unit,
    onDeleteAllDownloads: () -> Unit,
    initialTab: Int = 0,
) {
    var selectedTab by rememberSaveable { mutableStateOf(initialTab.coerceIn(0, 1)) }
    var pendingDelete by remember { mutableStateOf<DownloadInfo?>(null) }
    val tabs = listOf(
        stringResource(R.string.download_complete),
        stringResource(R.string.downloading),
    )

    MuseFlowScaffold(
        title = stringResource(R.string.download_manager),
        onBack = onBack,
        bottomBar = {
            if (selectedTab == 1) {
                DownloadActionsBar(
                    isDownloading = downloadingState.isDownloading,
                    onToggleAll = onToggleAllDownloads,
                    onDeleteAll = onDeleteAllDownloads,
                )
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .semantics { testTagsAsResourceId = true }
                .testTag(DOWNLOAD_SCREEN_MARKER)
                .padding(padding),
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) },
                    )
                }
            }

            when (selectedTab) {
                0 -> traceComposable("DLP.downloaded.list") {
                    DownloadedList(
                        songs = downloadedState.songs,
                        onSongClick = onDownloadedSongClick,
                    )
                }

                else -> traceComposable("DLP.downloading.list") {
                    DownloadingList(
                        downloads = downloadingState.downloads,
                        songTitleForDownload = songTitleForDownload,
                        onToggleDownload = onToggleDownload,
                        onDeleteClick = { pendingDelete = it },
                        onDownloadTerminalState = onDownloadTerminalState,
                    )
                }
            }
        }
    }

    pendingDelete?.let { download ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text(stringResource(R.string.confirm_delete)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        pendingDelete = null
                        onDeleteDownload(download)
                    },
                ) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }
}

@Composable
private fun DownloadedList(
    songs: List<Song>,
    onSongClick: (Song) -> Unit,
) {
    if (songs.isEmpty()) {
        EmptyContent(text = stringResource(R.string.no_downloaded_music))
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag(DOWNLOADED_LIST_MARKER),
        contentPadding = PaddingValues(vertical = 8.dp),
    ) {
        itemsIndexed(
            items = songs,
            key = { index, song -> song.id ?: "downloaded-$index" },
        ) { index, song ->
            DownloadedSongRow(
                index = index + 1,
                song = song,
                onClick = { onSongClick(song) },
            )
            HorizontalDivider()
        }
    }
}

@Composable
private fun DownloadingList(
    downloads: List<DownloadInfo>,
    songTitleForDownload: (DownloadInfo) -> String,
    onToggleDownload: (DownloadInfo) -> Unit,
    onDeleteClick: (DownloadInfo) -> Unit,
    onDownloadTerminalState: (DownloadInfo) -> Unit,
) {
    traceComposable("DLP.downloading.listBody") {
        if (downloads.isEmpty()) {
            EmptyContent(text = stringResource(R.string.no_downloading_task))
            return@traceComposable
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag(DOWNLOADING_LIST_MARKER),
            contentPadding = PaddingValues(vertical = 8.dp),
        ) {
            itemsIndexed(
                items = downloads,
                key = { index, download -> download.id ?: "downloading-$index" },
            ) { _, download ->
                traceComposable("DLP.downloading.item") {
                    val title = remember(download.id) {
                        traceSection("DLP.downloading.title") {
                            songTitleForDownload(download)
                        }
                    }
                    DownloadingTaskRow(
                        download = download,
                        title = title,
                        onClick = { onToggleDownload(download) },
                        onDeleteClick = { onDeleteClick(download) },
                        onDownloadTerminalState = { onDownloadTerminalState(download) },
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun DownloadedSongRow(
    index: Int,
    song: Song,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = index.toString(),
            modifier = Modifier.size(36.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = song.title.orEmpty(),
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = song.singer?.nickname ?: song.singerNickname.orEmpty(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun DownloadingTaskRow(
    download: DownloadInfo,
    title: String,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onDownloadTerminalState: () -> Unit,
) {
    traceComposable("DLP.downloading.row") {
        var refreshTick by remember(download) { mutableStateOf(0) }
        DisposableEffect(download) {
            val listener = object : MyDownloadListener() {
                override fun onRefresh() {
                    traceSection("DLP.downloading.listenerRefresh") {
                        refreshTick += 1
                        if (!download.isVisibleDownloadingState()) {
                            onDownloadTerminalState()
                        }
                    }
                }
            }
            download.downloadListener = listener
            onDispose {
                if (download.downloadListener === listener) {
                    download.downloadListener = null
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title.ifBlank { download.id.orEmpty() },
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(6.dp))
                DownloadStatus(
                    download = download,
                    refreshTick = refreshTick,
                )
            }
            TextButton(onClick = onDeleteClick) {
                Text(stringResource(R.string.delete))
            }
        }
    }
}

@Composable
private fun DownloadStatus(
    download: DownloadInfo,
    @Suppress("UNUSED_PARAMETER") refreshTick: Int,
) {
    traceComposable("DLP.downloading.status") {
        val statusText = when (download.status) {
            DownloadInfo.STATUS_PAUSED -> stringResource(R.string.click_download)
            DownloadInfo.STATUS_ERROR -> stringResource(R.string.download_failed)
            DownloadInfo.STATUS_WAIT -> stringResource(R.string.wait_download)
            DownloadInfo.STATUS_DOWNLOADING,
            DownloadInfo.STATUS_PREPARE_DOWNLOAD -> {
                val start = traceSection("DLP.downloading.formatStart") {
                    FileUtil.formatFileSize(download.progress)
                }
                val size = traceSection("DLP.downloading.formatSize") {
                    FileUtil.formatFileSize(download.size)
                }
                stringResource(R.string.download_progress, start, size)
            }

            else -> ""
        }
        val showProgress = download.status == DownloadInfo.STATUS_DOWNLOADING ||
            download.status == DownloadInfo.STATUS_PREPARE_DOWNLOAD

        Column {
            Text(
                text = statusText,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
            )
            if (showProgress && download.size > 0) {
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = {
                        traceSection("DLP.downloading.progressFraction") {
                            (download.progress.toFloat() / download.size.toFloat()).coerceIn(0f, 1f)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun DownloadActionsBar(
    isDownloading: Boolean,
    onToggleAll: () -> Unit,
    onDeleteAll: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Button(
            modifier = Modifier.weight(1f),
            onClick = onToggleAll,
        ) {
            Text(
                text = stringResource(
                    if (isDownloading) R.string.pause_all else R.string.download_all
                ),
            )
        }
        OutlinedButton(
            modifier = Modifier.weight(1f),
            onClick = onDeleteAll,
        ) {
            Text(stringResource(R.string.delete_all))
        }
    }
}

private fun DownloadInfo.isVisibleDownloadingState(): Boolean {
    return when (status) {
        DownloadInfo.STATUS_NONE,
        DownloadInfo.STATUS_PAUSED,
        DownloadInfo.STATUS_ERROR,
        DownloadInfo.STATUS_DOWNLOADING,
        DownloadInfo.STATUS_PREPARE_DOWNLOAD,
        DownloadInfo.STATUS_WAIT -> true

        else -> false
    }
}

private inline fun <T> traceSection(name: String, block: () -> T): T {
    Trace.beginSection(name)
    return try {
        block()
    } finally {
        Trace.endSection()
    }
}

@Composable
private inline fun traceComposable(name: String, content: @Composable () -> Unit) {
    Trace.beginSection(name)
    content()
    Trace.endSection()
}
