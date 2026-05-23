package com.ixuea.courses.mymusic.component.user.repository

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class UserNavigationEvents private constructor() {
    private val detailRequests = MutableSharedFlow<String?>(
        replay = 0,
        extraBufferCapacity = 16,
    )

    val detailRequested: SharedFlow<String?> = detailRequests.asSharedFlow()

    fun notifyDetailRequested(userId: String?) {
        detailRequests.tryEmit(userId)
    }

    companion object {
        @JvmField
        val INSTANCE = UserNavigationEvents()
    }
}
