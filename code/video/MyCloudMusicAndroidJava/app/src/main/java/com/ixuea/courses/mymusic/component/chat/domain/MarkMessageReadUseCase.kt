package com.ixuea.courses.mymusic.component.chat.domain

import com.ixuea.courses.mymusic.component.chat.repository.MessageRepository
import io.rong.imlib.model.Message

class MarkMessageReadUseCase(
    private val repository: MessageRepository = MessageRepository.INSTANCE,
) {
    operator fun invoke(message: Message) {
        repository.markRead(message)
    }
}
