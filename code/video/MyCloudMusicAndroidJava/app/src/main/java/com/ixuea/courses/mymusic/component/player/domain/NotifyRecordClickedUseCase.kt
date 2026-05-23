package com.ixuea.courses.mymusic.component.player.domain

import com.ixuea.courses.mymusic.component.player.repository.PlayerEvents

class NotifyRecordClickedUseCase(
    private val events: PlayerEvents = PlayerEvents.INSTANCE,
) {
    operator fun invoke() {
        events.notifyRecordClicked()
    }
}
