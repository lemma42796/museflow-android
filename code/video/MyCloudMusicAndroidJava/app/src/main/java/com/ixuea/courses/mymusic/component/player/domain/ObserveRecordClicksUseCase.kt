package com.ixuea.courses.mymusic.component.player.domain

import com.ixuea.courses.mymusic.component.player.repository.PlayerEvents
import kotlinx.coroutines.flow.Flow

class ObserveRecordClicksUseCase(
    private val events: PlayerEvents = PlayerEvents.INSTANCE,
) {
    operator fun invoke(): Flow<Unit> {
        return events.recordClicked
    }
}
