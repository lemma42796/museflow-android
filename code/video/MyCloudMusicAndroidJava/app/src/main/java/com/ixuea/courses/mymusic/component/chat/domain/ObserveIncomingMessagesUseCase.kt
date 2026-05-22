package com.ixuea.courses.mymusic.component.chat.domain

import com.ixuea.courses.mymusic.component.chat.repository.MessageRepository
import io.rong.imlib.model.Message
import kotlinx.coroutines.flow.Flow

class ObserveIncomingMessagesUseCase(
    private val repository: MessageRepository = MessageRepository.INSTANCE,
) {
    operator fun invoke(): Flow<Message> {
        return repository.messages
    }
}
