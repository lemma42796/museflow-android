package com.ixuea.courses.mymusic.component.player.repository

import com.ixuea.courses.mymusic.manager.model.MusicPlayListChange
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class PlayerEvents private constructor() {
    private val recordClicks = MutableSharedFlow<Unit>(
        replay = 0,
        extraBufferCapacity = 16,
    )
    private val playListChanges = MutableSharedFlow<MusicPlayListChange>(
        replay = 0,
        extraBufferCapacity = 16,
    )

    val recordClicked: SharedFlow<Unit> = recordClicks.asSharedFlow()
    val playListChanged: SharedFlow<MusicPlayListChange> = playListChanges.asSharedFlow()

    fun notifyRecordClicked() {
        recordClicks.tryEmit(Unit)
    }

    fun notifyPlayListChanged(position: Int) {
        playListChanges.tryEmit(MusicPlayListChange(position))
    }

    companion object {
        @JvmField
        val INSTANCE = PlayerEvents()
    }
}
