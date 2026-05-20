package com.ixuea.courses.mymusic.component.chat.domain

import com.ixuea.courses.mymusic.component.chat.repository.ChatClient
import com.ixuea.courses.mymusic.component.chat.repository.MessageRepository
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Message
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LoadChatHistoryUseCase(
    private val repository: MessageRepository = MessageRepository.INSTANCE,
) {
    suspend operator fun invoke(
        targetId: String,
        beforeMessageId: Int,
        count: Int,
    ): Result {
        return suspendCancellableCoroutine { continuation ->
            repository.getHistoryMessages(
                targetId,
                beforeMessageId,
                count,
                object : ChatClient.Callback<List<Message>> {
                    override fun onSuccess(data: List<Message>) {
                        if (continuation.isActive) {
                            continuation.resume(Result.Success(data))
                        }
                    }

                    override fun onError(errorCode: RongIMClient.ErrorCode?) {
                        if (continuation.isActive) {
                            continuation.resume(Result.Error(errorCode))
                        }
                    }
                }
            )
        }
    }

    sealed interface Result {
        data class Success(val messages: List<Message>) : Result
        data class Error(val errorCode: RongIMClient.ErrorCode?) : Result
    }
}
