package com.ixuea.courses.mymusic.component.download.domain

import com.ixuea.android.downloader.domain.DownloadInfo
import com.ixuea.courses.mymusic.component.download.repository.DownloadRepository

class LoadDownloadingUseCase(
    private val repository: DownloadRepository = DownloadRepository.getInstance(),
) {
    operator fun invoke(): List<DownloadInfo> {
        return repository.findDownloading()
    }
}
