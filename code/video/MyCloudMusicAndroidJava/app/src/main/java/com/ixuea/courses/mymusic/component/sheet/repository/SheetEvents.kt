package com.ixuea.courses.mymusic.component.sheet.repository

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class SheetEvents private constructor() {
    private val changes = MutableSharedFlow<Unit>(
        replay = 0,
        extraBufferCapacity = 16,
    )

    val changed: SharedFlow<Unit> = changes.asSharedFlow()

    fun notifyChanged() {
        changes.tryEmit(Unit)
    }

    companion object {
        @JvmField
        val INSTANCE = SheetEvents()
    }
}
