package com.ixuea.courses.mymusic.component.download.domain

import com.ixuea.android.downloader.domain.DownloadInfo
import com.ixuea.courses.mymusic.component.download.repository.DownloadRepository

class DownloadActionsUseCase(
    private val repository: DownloadRepository = DownloadRepository.getInstance(),
) {
    fun getDownloadById(id: String): DownloadInfo? {
        return repository.getDownloadById(id)
    }

    fun download(data: DownloadInfo) {
        repository.download(data)
    }

    fun resume(data: DownloadInfo) {
        repository.resume(data)
    }

    fun pause(data: DownloadInfo) {
        repository.pause(data)
    }

    fun remove(data: DownloadInfo) {
        repository.remove(data)
    }

    fun resumeAll() {
        repository.resumeAll()
    }

    fun pauseAll() {
        repository.pauseAll()
    }

    fun isDownloading(data: List<DownloadInfo>): Boolean {
        return repository.isDownloading(data)
    }
}
