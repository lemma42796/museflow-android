package com.ixuea.courses.mymusic.component.chat.domain

import com.ixuea.courses.mymusic.component.chat.repository.ConversationRepository
import kotlinx.coroutines.flow.Flow

class ObserveUnreadChangesUseCase(
    private val repository: ConversationRepository = ConversationRepository.INSTANCE,
) {
    operator fun invoke(): Flow<Unit> {
        return repository.unreadChanged
    }
}
