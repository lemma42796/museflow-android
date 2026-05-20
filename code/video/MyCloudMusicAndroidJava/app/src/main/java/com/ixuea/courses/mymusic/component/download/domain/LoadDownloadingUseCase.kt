package com.ixuea.courses.mymusic.component.download.domain

import com.ixuea.android.downloader.domain.DownloadInfo
import com.ixuea.courses.mymusic.component.download.repository.DownloadRepository

class LoadDownloadingUseCase(
    private val repository: DownloadRepository = DownloadRepository.getInstance(),
) {
    operator fun invoke(): List<DownloadInfo> {
        return repository.findDownloading().filter { it.isVisibleDownloadingState() }
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
}
