package com.ixuea.courses.mymusic.component.download.domain

import com.ixuea.courses.mymusic.component.download.repository.DownloadEvents

class NotifyDownloadedChangedUseCase(
    private val events: DownloadEvents = DownloadEvents.INSTANCE,
) {
    operator fun invoke() {
        events.notifyDownloadedChanged()
    }
}
