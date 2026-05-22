package com.ixuea.courses.mymusic.component.feed.domain

import com.ixuea.courses.mymusic.component.feed.repository.FeedEvents
import kotlinx.coroutines.flow.Flow

class ObserveFeedChangesUseCase(
    private val events: FeedEvents = FeedEvents.INSTANCE,
) {
    operator fun invoke(): Flow<Unit> {
        return events.changed
    }
}
