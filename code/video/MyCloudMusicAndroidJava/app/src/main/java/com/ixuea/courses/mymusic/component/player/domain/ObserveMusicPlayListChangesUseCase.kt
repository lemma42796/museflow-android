package com.ixuea.courses.mymusic.component.player.domain

import com.ixuea.courses.mymusic.component.player.repository.PlayerEvents
import com.ixuea.courses.mymusic.manager.model.MusicPlayListChange
import kotlinx.coroutines.flow.Flow

class ObserveMusicPlayListChangesUseCase(
    private val events: PlayerEvents = PlayerEvents.INSTANCE,
) {
    operator fun invoke(): Flow<MusicPlayListChange> {
        return events.playListChanged
    }
}
