package com.ixuea.courses.mymusic.component.discovery.repository

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class DiscoveryEvents private constructor() {
    private val sortChanges = MutableSharedFlow<Unit>(
        replay = 0,
        extraBufferCapacity = 16,
    )

    val sortChanged: SharedFlow<Unit> = sortChanges.asSharedFlow()

    fun notifySortChanged() {
        sortChanges.tryEmit(Unit)
    }

    companion object {
        @JvmField
        val INSTANCE = DiscoveryEvents()
    }
}
