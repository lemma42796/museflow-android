package com.ixuea.courses.mymusic.component.conversation.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ixuea.courses.mymusic.component.conversation.domain.DeleteConversationMessagesUseCase
import com.ixuea.courses.mymusic.component.conversation.domain.LoadConversationUserUseCase
import com.ixuea.courses.mymusic.component.conversation.domain.LoadConversationListUseCase
import com.ixuea.courses.mymusic.component.user.model.User
import com.ixuea.courses.mymusic.util.MessageUtil
import com.ixuea.courses.mymusic.util.StringUtil
import com.ixuea.courses.mymusic.util.SuperDateUtil
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ConversationListViewModel(
    private val loadConversationList: LoadConversationListUseCase = LoadConversationListUseCase(),
    private val deleteConversationMessages: DeleteConversationMessagesUseCase =
        DeleteConversationMessagesUseCase(),
    private val loadConversationUser: LoadConversationUserUseCase = LoadConversationUserUseCase(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(ConversationListUiState())
    val uiState: StateFlow<ConversationListUiState> = _uiState
    private var deferredRefreshJob: Job? = null

    fun load(context: Context) {
        val appContext = context.applicationContext
        _uiState.update {
            it.copy(
                isLoading = true,
                errorCode = null,
            )
        }

        viewModelScope.launch {
            when (val result = loadConversationList()) {
                is LoadConversationListUseCase.Result.Success -> publish(
                    context = appContext,
                    conversations = result.conversations,
                )
                is LoadConversationListUseCase.Result.Error -> publishError(result.errorCode)
            }
        }
    }

    fun refreshAfterNewMessage(context: Context) {
        if (deferredRefreshJob?.isActive == true) {
            return
        }

        deferredRefreshJob = viewModelScope.launch {
            delay(1000)
            load(context.applicationContext)
        }
    }

    fun deleteMessages(context: Context, conversation: Conversation) {
        viewModelScope.launch {
            when (val result = deleteConversationMessages(conversation)) {
                is DeleteConversationMessagesUseCase.Result.Success -> load(context.applicationContext)
                is DeleteConversationMessagesUseCase.Result.Error -> publishError(result.errorCode)
            }
        }
    }

    private fun publish(context: Context, conversations: List<Conversation>) {
        val items = conversations.map { it.toUiState() }
        _uiState.update {
            it.copy(
                isLoading = false,
                conversations = items,
                dataVersion = it.dataVersion + 1,
            )
        }
        conversations.forEach { conversation ->
            resolveUser(context, conversation.targetId)
        }
    }

    private fun resolveUser(context: Context, targetId: String) {
        viewModelScope.launch {
            runCatching {
                loadConversationUser(context, targetId)
            }.onSuccess { user ->
                publishUser(targetId, user)
            }
        }
    }

    private fun publishUser(targetId: String, user: User) {
        _uiState.update { state ->
            val items = state.conversations.map { item ->
                if (item.targetId == targetId) {
                    item.copy(
                        nickname = user.nickname.orEmpty(),
                        icon = user.icon.orEmpty(),
                    )
                } else {
                    item
                }
            }
            state.copy(
                conversations = items,
                dataVersion = state.dataVersion + 1,
            )
        }
    }

    private fun Conversation.toUiState(): ConversationItemUiState {
        val latestMessage = latestMessage
        val time = if (latestMessage == null) {
            ""
        } else {
            SuperDateUtil.commonFormat(receivedTime)
        }
        val message = if (latestMessage == null) {
            ""
        } else {
            MessageUtil.getContent(latestMessage)
        }
        val unread = if (unreadMessageCount > 0) {
            StringUtil.formatMessageCount(unreadMessageCount)
        } else {
            ""
        }

        return ConversationItemUiState(
            conversation = this,
            targetId = targetId,
            nickname = targetId.orEmpty(),
            icon = "",
            timeText = time,
            messageText = message,
            unreadText = unread,
        )
    }

    private fun publishError(errorCode: RongIMClient.ErrorCode?) {
        _uiState.update {
            it.copy(
                isLoading = false,
                errorCode = errorCode,
                errorVersion = it.errorVersion + 1,
            )
        }
    }
}
