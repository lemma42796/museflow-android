package com.ixuea.courses.mymusic.component.download.ui

import android.os.Trace
import androidx.lifecycle.ViewModel
import com.ixuea.android.downloader.domain.DownloadInfo
import com.ixuea.courses.mymusic.component.download.domain.DownloadActionsUseCase
import com.ixuea.courses.mymusic.component.download.domain.LoadDownloadingUseCase
import com.ixuea.courses.mymusic.component.download.domain.NotifyDownloadedChangedUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class DownloadingViewModel(
    private val loadDownloading: LoadDownloadingUseCase = LoadDownloadingUseCase(),
    private val downloadActions: DownloadActionsUseCase = DownloadActionsUseCase(),
    private val notifyDownloadedChanged: NotifyDownloadedChangedUseCase = NotifyDownloadedChangedUseCase(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(DownloadingUiState())
    val uiState: StateFlow<DownloadingUiState> = _uiState

    fun load() {
        publishDownloads()
    }

    fun onDownloadTerminalState() {
        publishDownloads()
        notifyDownloadedChanged()
    }

    fun toggle(data: DownloadInfo) {
        when (data.status) {
            DownloadInfo.STATUS_NONE,
            DownloadInfo.STATUS_PAUSED,
            DownloadInfo.STATUS_ERROR -> downloadActions.resume(data)

            else -> downloadActions.pause(data)
        }
        publishDownloads()
    }

    fun remove(data: DownloadInfo) {
        downloadActions.remove(data)
        publishDownloads()
    }

    fun removeAll(data: List<DownloadInfo>) {
        data.forEach(downloadActions::remove)
        publishDownloads()
    }

    fun resumeAll() {
        downloadActions.resumeAll()
        publishDownloads()
    }

    fun pauseAll() {
        downloadActions.pauseAll()
        publishDownloads()
    }

    private fun publishDownloads() {
        traceSection("DLP.vm.publishDownloads") {
            val downloads = traceSection("DLP.vm.loadDownloading") {
                loadDownloading()
            }
            val isDownloading = traceSection("DLP.vm.isDownloading") {
                downloadActions.isDownloading(downloads)
            }
            traceSection("DLP.vm.updateState") {
                _uiState.update {
                    it.copy(
                        downloads = downloads,
                        isDownloading = isDownloading,
                        dataVersion = it.dataVersion + 1,
                    )
                }
            }
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
