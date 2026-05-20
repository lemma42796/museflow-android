package com.ixuea.courses.mymusic.component.download.ui

import com.ixuea.android.downloader.domain.DownloadInfo

data class DownloadingUiState(
    val downloads: List<DownloadInfo> = emptyList(),
    val isDownloading: Boolean = false,
    val dataVersion: Long = 0,
)
