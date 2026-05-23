package com.ixuea.courses.mymusic.component.download.domain

import com.ixuea.courses.mymusic.component.download.repository.DownloadEvents
import kotlinx.coroutines.flow.Flow

class ObserveDownloadedChangesUseCase(
    private val events: DownloadEvents = DownloadEvents.INSTANCE,
) {
    operator fun invoke(): Flow<Unit> {
        return events.downloadedChanged
    }
}
