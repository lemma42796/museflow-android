package com.ixuea.courses.mymusic.component.conversation.domain

import com.ixuea.courses.mymusic.component.chat.repository.ChatClient
import com.ixuea.courses.mymusic.component.chat.repository.ConversationRepository
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LoadConversationListUseCase(
    private val repository: ConversationRepository = ConversationRepository.INSTANCE,
) {
    suspend operator fun invoke(): Result {
        return suspendCancellableCoroutine { continuation ->
            repository.getConversationList(
                object : ChatClient.Callback<List<Conversation>> {
                    override fun onSuccess(data: List<Conversation>) {
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
        data class Success(val conversations: List<Conversation>) : Result
        data class Error(val errorCode: RongIMClient.ErrorCode?) : Result
    }
}
