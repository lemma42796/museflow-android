package com.ixuea.courses.mymusic.component.conversation.domain

import com.ixuea.courses.mymusic.component.chat.repository.ChatClient
import com.ixuea.courses.mymusic.component.chat.repository.ConversationRepository
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class DeleteConversationMessagesUseCase(
    private val repository: ConversationRepository = ConversationRepository.INSTANCE,
) {
    suspend operator fun invoke(conversation: Conversation): Result {
        return suspendCancellableCoroutine { continuation ->
            repository.deleteMessages(
                conversation.conversationType,
                conversation.targetId,
                object : ChatClient.Callback<Boolean> {
                    override fun onSuccess(data: Boolean) {
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
        data class Success(val deleted: Boolean) : Result
        data class Error(val errorCode: RongIMClient.ErrorCode?) : Result
    }
}
