package com.ixuea.courses.mymusic.component.chat.ui

import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Message

data class ChatUiState(
    val targetTitle: String = "",
    val targetTitleVersion: Long = 0,
    val isLoadingHistory: Boolean = false,
    val messages: List<ChatMessageUiState> = emptyList(),
    val dataVersion: Long = 0,
    val scrollToBottomVersion: Long = 0,
    val smoothScrollBottomVersion: Long = 0,
    val errorCode: RongIMClient.ErrorCode? = null,
    val errorVersion: Long = 0,
    val sendOperation: ChatSendOperation = ChatSendOperation.NONE,
    val imageSendProgress: Int = 0,
    val sendErrorMessage: Message? = null,
    val sendErrorCode: RongIMClient.ErrorCode? = null,
    val sendError: Throwable? = null,
    val sendErrorVersion: Long = 0,
    val clearInputVersion: Long = 0,
    val unreadClearedVersion: Long = 0,
    val unreadClearErrorCode: RongIMClient.ErrorCode? = null,
    val unreadClearErrorVersion: Long = 0,
)
