package com.ixuea.courses.mymusic.component.music.domain

import com.ixuea.courses.mymusic.component.music.repository.LocalMusicEvents

class NotifyLocalMusicScanCompleteUseCase(
    private val events: LocalMusicEvents = LocalMusicEvents.INSTANCE,
) {
    operator fun invoke() {
        events.notifyScanCompleted()
    }
}
