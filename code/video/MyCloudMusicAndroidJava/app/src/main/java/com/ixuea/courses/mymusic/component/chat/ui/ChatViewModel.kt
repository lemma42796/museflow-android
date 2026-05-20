package com.ixuea.courses.mymusic.component.chat.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ixuea.courses.mymusic.component.chat.domain.ClearConversationUnreadUseCase
import com.ixuea.courses.mymusic.component.chat.domain.LoadChatHistoryUseCase
import com.ixuea.courses.mymusic.component.chat.domain.LoadChatUserUseCase
import com.ixuea.courses.mymusic.component.chat.domain.MarkMessageReadUseCase
import com.ixuea.courses.mymusic.component.chat.domain.SendImageMessageUseCase
import com.ixuea.courses.mymusic.component.chat.domain.SendTextMessageUseCase
import com.ixuea.courses.mymusic.component.user.model.User
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel(
    private val loadChatHistory: LoadChatHistoryUseCase = LoadChatHistoryUseCase(),
    private val sendTextMessage: SendTextMessageUseCase = SendTextMessageUseCase(),
    private val sendImageMessage: SendImageMessageUseCase = SendImageMessageUseCase(),
    private val clearConversationUnread: ClearConversationUnreadUseCase =
        ClearConversationUnreadUseCase(),
    private val markMessageRead: MarkMessageReadUseCase = MarkMessageReadUseCase(),
    private val loadChatUser: LoadChatUserUseCase = LoadChatUserUseCase(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState

    private var oldestMessageId = -1
    private var targetUserId = ""
    private val senderIcons = mutableMapOf<String, String>()
    private val resolvingUserIds = mutableSetOf<String>()

    fun loadInitial(context: Context, targetId: String, count: Int) {
        loadTargetUser(context, targetId)
        if (_uiState.value.messages.isEmpty()) {
            loadMore(context, targetId, count)
        }
    }

    private fun loadTargetUser(context: Context, userId: String) {
        if (userId.isBlank() || (targetUserId == userId && _uiState.value.targetTitle.isNotBlank())) {
            return
        }

        targetUserId = userId
        viewModelScope.launch {
            runCatching {
                loadChatUser(context.applicationContext, userId)
            }.onSuccess { user ->
                publishTargetUser(userId, user)
            }
        }
    }

    fun loadMore(context: Context, targetId: String, count: Int) {
        if (_uiState.value.isLoadingHistory) {
            return
        }

        val isInitialLoad = oldestMessageId == -1
        _uiState.update {
            it.copy(
                isLoadingHistory = true,
                errorCode = null,
            )
        }

        viewModelScope.launch {
            when (val result = loadChatHistory(targetId, oldestMessageId, count)) {
                is LoadChatHistoryUseCase.Result.Success -> publishHistory(
                    context = context.applicationContext,
                    messages = result.messages,
                    shouldScrollToBottom = isInitialLoad,
                )

                is LoadChatHistoryUseCase.Result.Error -> publishError(result.errorCode)
            }
        }
    }

    fun appendMessage(context: Context, message: Message) {
        publishMessage(context.applicationContext, message, shouldClearInput = false)
    }

    fun appendIncomingMessage(context: Context, message: Message) {
        runCatching {
            markMessageRead(message)
        }
        appendMessage(context, message)
    }

    fun clearUnread(targetId: String) {
        viewModelScope.launch {
            try {
                when (val result = clearConversationUnread(targetId)) {
                    is ClearConversationUnreadUseCase.Result.Success -> {
                        _uiState.update {
                            it.copy(unreadClearedVersion = it.unreadClearedVersion + 1)
                        }
                    }

                    is ClearConversationUnreadUseCase.Result.Error -> {
                        publishUnreadClearError(result.errorCode)
                    }
                }
            } catch (error: Throwable) {
                publishUnreadClearError(null)
            }
        }
    }

    fun sendText(context: Context, targetId: String, content: String, senderUserId: String) {
        if (_uiState.value.sendOperation != ChatSendOperation.NONE) {
            return
        }

        _uiState.update {
            it.copy(
                sendOperation = ChatSendOperation.SENDING_TEXT,
                sendErrorMessage = null,
                sendErrorCode = null,
                sendError = null,
            )
        }

        viewModelScope.launch {
            try {
                when (val result = sendTextMessage(targetId, content, senderUserId)) {
                    is SendTextMessageUseCase.Result.Success -> publishMessage(
                        context = context.applicationContext,
                        message = result.message,
                        shouldClearInput = true,
                    )

                    is SendTextMessageUseCase.Result.Error -> publishSendError(
                        message = result.message,
                        errorCode = result.errorCode,
                    )
                }
            } catch (error: Throwable) {
                publishSendError(message = null, errorCode = null, error = error)
            }
        }
    }

    fun sendImage(context: Context, targetId: String, path: String, senderUserId: String) {
        if (_uiState.value.sendOperation != ChatSendOperation.NONE) {
            return
        }

        _uiState.update {
            it.copy(
                sendOperation = ChatSendOperation.SENDING_IMAGE,
                imageSendProgress = 0,
                sendErrorMessage = null,
                sendErrorCode = null,
                sendError = null,
            )
        }

        viewModelScope.launch {
            try {
                when (
                    val result = sendImageMessage(
                        targetId = targetId,
                        path = path,
                        senderUserId = senderUserId,
                        onProgress = { _, progress ->
                            publishImageProgress(progress)
                        },
                    )
                ) {
                    is SendImageMessageUseCase.Result.Success -> publishMessage(
                        context = context.applicationContext,
                        message = result.message,
                        shouldClearInput = false,
                    )

                    is SendImageMessageUseCase.Result.Error -> publishSendError(
                        message = result.message,
                        errorCode = result.errorCode,
                    )
                }
            } catch (error: Throwable) {
                publishSendError(message = null, errorCode = null, error = error)
            }
        }
    }

    private fun publishHistory(
        context: Context,
        messages: List<Message>,
        shouldScrollToBottom: Boolean,
    ) {
        if (messages.isEmpty()) {
            _uiState.update {
                it.copy(isLoadingHistory = false)
            }
            return
        }

        oldestMessageId = messages.first().messageId
        val items = messages.map { it.toUiState() }
        _uiState.update {
            it.copy(
                isLoadingHistory = false,
                messages = items + it.messages,
                dataVersion = it.dataVersion + 1,
                scrollToBottomVersion = if (shouldScrollToBottom) {
                    it.scrollToBottomVersion + 1
                } else {
                    it.scrollToBottomVersion
                },
            )
        }
        messages.forEach { message ->
            resolveUser(context, message.senderUserId)
        }
    }

    private fun publishError(errorCode: RongIMClient.ErrorCode?) {
        _uiState.update {
            it.copy(
                isLoadingHistory = false,
                errorCode = errorCode,
                errorVersion = it.errorVersion + 1,
            )
        }
    }

    private fun publishUnreadClearError(errorCode: RongIMClient.ErrorCode?) {
        _uiState.update {
            it.copy(
                unreadClearErrorCode = errorCode,
                unreadClearErrorVersion = it.unreadClearErrorVersion + 1,
            )
        }
    }

    private fun publishTargetUser(userId: String, user: User) {
        _uiState.update {
            it.copy(
                targetTitle = user.nickname.orEmpty(),
                targetTitleVersion = it.targetTitleVersion + 1,
            )
        }
        publishUser(userId, user)
    }

    private fun publishMessage(context: Context, message: Message, shouldClearInput: Boolean) {
        _uiState.update {
            it.copy(
                sendOperation = ChatSendOperation.NONE,
                imageSendProgress = 0,
                messages = it.messages + message.toUiState(),
                dataVersion = it.dataVersion + 1,
                smoothScrollBottomVersion = it.smoothScrollBottomVersion + 1,
                clearInputVersion = if (shouldClearInput) {
                    it.clearInputVersion + 1
                } else {
                    it.clearInputVersion
                },
            )
        }
        resolveUser(context, message.senderUserId)
    }

    private fun resolveUser(context: Context, userId: String?) {
        if (
            userId.isNullOrBlank() ||
            senderIcons.containsKey(userId) ||
            !resolvingUserIds.add(userId)
        ) {
            return
        }

        viewModelScope.launch {
            runCatching {
                loadChatUser(context.applicationContext, userId)
            }.onSuccess { user ->
                publishUser(userId, user)
            }.onFailure {
                resolvingUserIds.remove(userId)
            }
        }
    }

    private fun publishUser(userId: String, user: User) {
        val icon = user.icon.orEmpty()
        senderIcons[userId] = icon
        _uiState.update { state ->
            val messages = state.messages.map { item ->
                if (item.senderUserId == userId) {
                    item.copy(senderIcon = icon)
                } else {
                    item
                }
            }
            state.copy(
                messages = messages,
                dataVersion = state.dataVersion + 1,
            )
        }
    }

    private fun Message.toUiState(): ChatMessageUiState {
        val userId = senderUserId.orEmpty()
        return ChatMessageUiState(
            message = this,
            senderUserId = userId,
            senderIcon = senderIcons[userId].orEmpty(),
        )
    }

    private fun publishImageProgress(progress: Int) {
        _uiState.update {
            it.copy(imageSendProgress = progress.coerceIn(0, 100))
        }
    }

    private fun publishSendError(
        message: Message?,
        errorCode: RongIMClient.ErrorCode?,
        error: Throwable? = null,
    ) {
        _uiState.update {
            it.copy(
                sendOperation = ChatSendOperation.NONE,
                imageSendProgress = 0,
                sendErrorMessage = message,
                sendErrorCode = errorCode,
                sendError = error,
                sendErrorVersion = it.sendErrorVersion + 1,
            )
        }
    }
}
