package com.ixuea.courses.mymusic.component.conversation.ui

import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation

data class ConversationListUiState(
    val isLoading: Boolean = false,
    val conversations: List<ConversationItemUiState> = emptyList(),
    val dataVersion: Long = 0,
    val errorCode: RongIMClient.ErrorCode? = null,
    val errorVersion: Long = 0,
)
