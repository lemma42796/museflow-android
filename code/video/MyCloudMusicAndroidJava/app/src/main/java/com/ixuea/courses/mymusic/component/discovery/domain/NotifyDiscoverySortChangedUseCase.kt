package com.ixuea.courses.mymusic.component.discovery.domain

import com.ixuea.courses.mymusic.component.discovery.repository.DiscoveryEvents

class NotifyDiscoverySortChangedUseCase(
    private val events: DiscoveryEvents = DiscoveryEvents.INSTANCE,
) {
    operator fun invoke() {
        events.notifySortChanged()
    }
}
