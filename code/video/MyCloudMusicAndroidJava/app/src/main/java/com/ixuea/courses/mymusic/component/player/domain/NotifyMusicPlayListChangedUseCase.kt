package com.ixuea.courses.mymusic.component.player.domain

import com.ixuea.courses.mymusic.component.player.repository.PlayerEvents

class NotifyMusicPlayListChangedUseCase(
    private val events: PlayerEvents = PlayerEvents.INSTANCE,
) {
    operator fun invoke(position: Int) {
        events.notifyPlayListChanged(position)
    }
}
