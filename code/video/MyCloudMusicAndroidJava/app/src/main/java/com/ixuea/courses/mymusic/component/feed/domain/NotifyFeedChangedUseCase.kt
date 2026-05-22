package com.ixuea.courses.mymusic.component.feed.domain

import com.ixuea.courses.mymusic.component.feed.repository.FeedEvents

class NotifyFeedChangedUseCase(
    private val events: FeedEvents = FeedEvents.INSTANCE,
) {
    operator fun invoke() {
        events.notifyChanged()
    }
}
