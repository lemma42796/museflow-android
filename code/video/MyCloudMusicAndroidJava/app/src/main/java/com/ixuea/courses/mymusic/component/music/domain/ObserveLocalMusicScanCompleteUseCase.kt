package com.ixuea.courses.mymusic.component.music.domain

import com.ixuea.courses.mymusic.component.music.repository.LocalMusicEvents
import kotlinx.coroutines.flow.Flow

class ObserveLocalMusicScanCompleteUseCase(
    private val events: LocalMusicEvents = LocalMusicEvents.INSTANCE,
) {
    operator fun invoke(): Flow<Unit> {
        return events.scanCompleted
    }
}
