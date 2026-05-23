package com.ixuea.courses.mymusic.component.sheet.domain

import com.ixuea.courses.mymusic.component.sheet.repository.SheetEvents
import kotlinx.coroutines.flow.Flow

class ObserveSheetChangesUseCase(
    private val events: SheetEvents = SheetEvents.INSTANCE,
) {
    operator fun invoke(): Flow<Unit> {
        return events.changed
    }
}
