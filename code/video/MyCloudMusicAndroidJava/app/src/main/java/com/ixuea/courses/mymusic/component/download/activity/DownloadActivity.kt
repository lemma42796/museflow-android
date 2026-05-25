package com.ixuea.courses.mymusic.component.download.activity

import android.os.Bundle
import android.os.Trace
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.activity.BaseLogicActivity
import com.ixuea.courses.mymusic.component.download.ui.DOWNLOAD_INITIAL_TAB_EXTRA
import com.ixuea.courses.mymusic.component.download.ui.DownloadScreen
import com.ixuea.courses.mymusic.component.download.ui.DownloadedViewModel
import com.ixuea.courses.mymusic.component.download.ui.DownloadingViewModel
import com.ixuea.courses.mymusic.ui.compose.MuseFlowTheme
import com.ixuea.superui.toast.SuperToast

/**
 * Download manager screen backed by Compose.
 */
class DownloadActivity : BaseLogicActivity() {
    private lateinit var downloadedViewModel: DownloadedViewModel
    private lateinit var downloadingViewModel: DownloadingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        traceSection("DLP.activity.viewModels") {
            downloadedViewModel = ViewModelProvider(this)[DownloadedViewModel::class.java]
            downloadingViewModel = ViewModelProvider(this)[DownloadingViewModel::class.java]
        }

        traceSection("DLP.activity.setContent") {
            setContent {
                val downloadedState by downloadedViewModel.uiState.collectAsState()
                val downloadingState by downloadingViewModel.uiState.collectAsState()

                MuseFlowTheme {
                    DownloadScreen(
                        downloadedState = downloadedState,
                        downloadingState = downloadingState,
                        songTitleForDownload = { download ->
                            traceSection("DLP.activity.songTitleLookup") {
                                orm.querySong(download.id)?.title.orEmpty()
                            }
                        },
                        onBack = { onBackPressedDispatcher.onBackPressed() },
                        onDownloadedSongClick = { song ->
                            musicListManager.datum = downloadedState.songs
                            musicListManager.play(song)
                            startMusicPlayerActivity()
                        },
                        onToggleDownload = downloadingViewModel::toggle,
                        onDeleteDownload = downloadingViewModel::remove,
                        onDownloadTerminalState = {
                            downloadingViewModel.onDownloadTerminalState()
                        },
                        onToggleAllDownloads = {
                            if (downloadingState.downloads.isEmpty()) {
                                SuperToast.show(R.string.error_not_download)
                            } else if (downloadingState.isDownloading) {
                                downloadingViewModel.pauseAll()
                            } else {
                                downloadingViewModel.resumeAll()
                            }
                        },
                        onDeleteAllDownloads = {
                            if (downloadingState.downloads.isEmpty()) {
                                SuperToast.show(R.string.error_not_download)
                            } else {
                                downloadingViewModel.removeAll(downloadingState.downloads)
                            }
                        },
                        initialTab = intent.getIntExtra(DOWNLOAD_INITIAL_TAB_EXTRA, 0),
                    )
                }
            }
        }
    }

    override fun initDatum() {
        traceSection("DLP.activity.initDatum") {
            super.initDatum()
            downloadedViewModel.observeDownloadedChanges(orm)
            downloadedViewModel.load(orm)
            downloadingViewModel.load()
        }
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
