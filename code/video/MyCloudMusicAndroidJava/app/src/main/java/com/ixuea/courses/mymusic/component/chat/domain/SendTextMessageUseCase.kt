package com.ixuea.courses.mymusic.component.chat.domain

import com.ixuea.courses.mymusic.component.chat.repository.ChatClient
import com.ixuea.courses.mymusic.component.chat.repository.MessageRepository
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Message
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class SendTextMessageUseCase(
    private val repository: MessageRepository = MessageRepository.INSTANCE,
) {
    suspend operator fun invoke(
        targetId: String,
        content: String,
        senderUserId: String,
    ): Result {
        return suspendCancellableCoroutine { continuation ->
            repository.sendText(
                targetId,
                content,
                senderUserId,
                object : ChatClient.SendCallback {
                    override fun onAttached(message: Message) {
                    }

                    override fun onSuccess(message: Message) {
                        if (continuation.isActive) {
                            continuation.resume(Result.Success(message))
                        }
                    }

                    override fun onError(message: Message?, errorCode: RongIMClient.ErrorCode?) {
                        if (continuation.isActive) {
                            continuation.resume(Result.Error(message, errorCode))
                        }
                    }
                }
            )
        }
    }

    sealed interface Result {
        data class Success(val message: Message) : Result
        data class Error(
            val message: Message?,
            val errorCode: RongIMClient.ErrorCode?,
        ) : Result
    }
}
