package com.ixuea.courses.mymusic.component.sheet.domain

import com.ixuea.courses.mymusic.component.sheet.repository.SheetEvents

class NotifySheetChangedUseCase(
    private val events: SheetEvents = SheetEvents.INSTANCE,
) {
    operator fun invoke() {
        events.notifyChanged()
    }
}
