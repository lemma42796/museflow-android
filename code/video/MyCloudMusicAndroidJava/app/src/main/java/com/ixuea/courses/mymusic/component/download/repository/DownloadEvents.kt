package com.ixuea.courses.mymusic.component.download.repository

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class DownloadEvents private constructor() {
    private val downloadedChanges = MutableSharedFlow<Unit>(
        replay = 0,
        extraBufferCapacity = 16,
    )

    val downloadedChanged: SharedFlow<Unit> = downloadedChanges.asSharedFlow()

    fun notifyDownloadedChanged() {
        downloadedChanges.tryEmit(Unit)
    }

    companion object {
        @JvmField
        val INSTANCE = DownloadEvents()
    }
}
