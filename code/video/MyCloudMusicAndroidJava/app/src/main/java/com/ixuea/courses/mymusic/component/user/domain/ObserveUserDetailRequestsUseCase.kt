package com.ixuea.courses.mymusic.component.user.domain

import com.ixuea.courses.mymusic.component.user.repository.UserNavigationEvents
import kotlinx.coroutines.flow.Flow

class ObserveUserDetailRequestsUseCase(
    private val events: UserNavigationEvents = UserNavigationEvents.INSTANCE,
) {
    operator fun invoke(): Flow<String?> {
        return events.detailRequested
    }
}
