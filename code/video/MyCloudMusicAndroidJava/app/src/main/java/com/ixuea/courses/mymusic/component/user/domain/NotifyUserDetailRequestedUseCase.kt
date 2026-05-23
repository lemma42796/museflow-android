package com.ixuea.courses.mymusic.component.user.domain

import com.ixuea.courses.mymusic.component.user.repository.UserNavigationEvents

class NotifyUserDetailRequestedUseCase(
    private val events: UserNavigationEvents = UserNavigationEvents.INSTANCE,
) {
    operator fun invoke(userId: String?) {
        events.notifyDetailRequested(userId)
    }
}
