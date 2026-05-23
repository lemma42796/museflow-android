package com.ixuea.courses.mymusic.component.download.ui

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
        val downloads = loadDownloading()
        _uiState.update {
            it.copy(
                downloads = downloads,
                isDownloading = downloadActions.isDownloading(downloads),
                dataVersion = it.dataVersion + 1,
            )
        }
    }
}
