package com.ixuea.courses.mymusic.component.chat.domain

import com.ixuea.courses.mymusic.component.chat.repository.ChatClient
import com.ixuea.courses.mymusic.component.chat.repository.MessageRepository
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Message
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class SendImageMessageUseCase(
    private val repository: MessageRepository = MessageRepository.INSTANCE,
) {
    suspend operator fun invoke(
        targetId: String,
        path: String,
        senderUserId: String,
        onProgress: (Message, Int) -> Unit,
    ): Result {
        return suspendCancellableCoroutine { continuation ->
            repository.sendImage(
                targetId,
                path,
                senderUserId,
                object : ChatClient.ImageSendCallback {
                    override fun onAttached(message: Message) {
                    }

                    override fun onError(message: Message?, errorCode: RongIMClient.ErrorCode?) {
                        if (continuation.isActive) {
                            continuation.resume(Result.Error(message, errorCode))
                        }
                    }

                    override fun onSuccess(message: Message) {
                        if (continuation.isActive) {
                            continuation.resume(Result.Success(message))
                        }
                    }

                    override fun onProgress(message: Message, progress: Int) {
                        if (continuation.isActive) {
                            onProgress(message, progress)
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
