package com.ixuea.courses.mymusic.component.chat.domain

import com.ixuea.courses.mymusic.component.chat.repository.ChatClient
import com.ixuea.courses.mymusic.component.chat.repository.ConversationRepository
import io.rong.imlib.RongIMClient
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class ClearConversationUnreadUseCase(
    private val repository: ConversationRepository = ConversationRepository.INSTANCE,
) {
    suspend operator fun invoke(targetId: String): Result {
        return suspendCancellableCoroutine { continuation ->
            repository.clearUnread(
                targetId,
                object : ChatClient.Callback<Boolean> {
                    override fun onSuccess(data: Boolean) {
                        if (continuation.isActive) {
                            continuation.resume(Result.Success)
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
        data object Success : Result
        data class Error(val errorCode: RongIMClient.ErrorCode?) : Result
    }
}
