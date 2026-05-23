package com.ixuea.courses.mymusic.component.discovery.domain

import com.ixuea.courses.mymusic.component.discovery.repository.DiscoveryEvents
import kotlinx.coroutines.flow.Flow

class ObserveDiscoverySortChangesUseCase(
    private val events: DiscoveryEvents = DiscoveryEvents.INSTANCE,
) {
    operator fun invoke(): Flow<Unit> {
        return events.sortChanged
    }
}
