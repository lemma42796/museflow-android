package com.ixuea.courses.mymusic.component.conversation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ixuea.courses.mymusic.component.conversation.domain.DeleteConversationMessagesUseCase
import com.ixuea.courses.mymusic.component.conversation.domain.LoadConversationListUseCase
import com.ixuea.courses.mymusic.component.chat.domain.ObserveIncomingMessagesUseCase
import com.ixuea.courses.mymusic.component.chat.domain.ObserveUnreadChangesUseCase
import com.ixuea.courses.mymusic.component.user.domain.LoadUserDetailUseCase
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
    private val loadUserDetail: LoadUserDetailUseCase = LoadUserDetailUseCase(),
    private val observeIncomingMessagesUseCase: ObserveIncomingMessagesUseCase =
        ObserveIncomingMessagesUseCase(),
    private val observeUnreadChangesUseCase: ObserveUnreadChangesUseCase =
        ObserveUnreadChangesUseCase(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(ConversationListUiState())
    val uiState: StateFlow<ConversationListUiState> = _uiState
    private var deferredRefreshJob: Job? = null
    private var incomingMessagesJob: Job? = null
    private var unreadChangesJob: Job? = null

    fun load() {
        _uiState.update {
            it.copy(
                isLoading = true,
                errorCode = null,
            )
        }

        viewModelScope.launch {
            when (val result = loadConversationList()) {
                is LoadConversationListUseCase.Result.Success -> publish(
                    conversations = result.conversations,
                )
                is LoadConversationListUseCase.Result.Error -> publishError(result.errorCode)
            }
        }
    }

    fun observeConversationChanges() {
        if (incomingMessagesJob?.isActive != true) {
            incomingMessagesJob = viewModelScope.launch {
                observeIncomingMessagesUseCase().collect {
                    refreshAfterNewMessage()
                }
            }
        }

        if (unreadChangesJob?.isActive != true) {
            unreadChangesJob = viewModelScope.launch {
                observeUnreadChangesUseCase().collect {
                    refreshAfterNewMessage()
                }
            }
        }
    }

    fun refreshAfterNewMessage() {
        if (deferredRefreshJob?.isActive == true) {
            return
        }

        deferredRefreshJob = viewModelScope.launch {
            delay(1000)
            load()
        }
    }

    fun deleteMessages(conversation: Conversation) {
        viewModelScope.launch {
            when (val result = deleteConversationMessages(conversation)) {
                is DeleteConversationMessagesUseCase.Result.Success -> load()
                is DeleteConversationMessagesUseCase.Result.Error -> publishError(result.errorCode)
            }
        }
    }

    private fun publish(conversations: List<Conversation>) {
        val items = conversations.map { it.toUiState() }
        _uiState.update {
            it.copy(
                isLoading = false,
                conversations = items,
                dataVersion = it.dataVersion + 1,
            )
        }
        conversations.forEach { conversation ->
            resolveUser(conversation.targetId)
        }
    }

    private fun resolveUser(targetId: String) {
        viewModelScope.launch {
            when (val result = loadUserDetail(targetId)) {
                is LoadUserDetailUseCase.Result.Success -> publishUser(targetId, result.user)
                is LoadUserDetailUseCase.Result.Error -> Unit
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
