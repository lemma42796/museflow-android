package com.ixuea.courses.mymusic.component.chat.ui

import io.rong.imlib.model.Message

data class ChatMessageUiState(
    val message: Message,
    val senderUserId: String,
    val senderIcon: String,
)
