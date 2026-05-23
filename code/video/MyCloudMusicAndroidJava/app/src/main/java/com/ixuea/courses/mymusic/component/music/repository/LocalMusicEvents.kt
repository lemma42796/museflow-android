package com.ixuea.courses.mymusic.component.music.repository

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class LocalMusicEvents private constructor() {
    private val scanCompletions = MutableSharedFlow<Unit>(
        replay = 0,
        extraBufferCapacity = 16,
    )

    val scanCompleted: SharedFlow<Unit> = scanCompletions.asSharedFlow()

    fun notifyScanCompleted() {
        scanCompletions.tryEmit(Unit)
    }

    companion object {
        @JvmField
        val INSTANCE = LocalMusicEvents()
    }
}
