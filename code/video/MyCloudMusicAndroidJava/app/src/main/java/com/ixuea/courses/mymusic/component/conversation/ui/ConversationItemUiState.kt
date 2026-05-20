package com.ixuea.courses.mymusic.component.conversation.ui

import io.rong.imlib.model.Conversation

data class ConversationItemUiState(
    val conversation: Conversation,
    val targetId: String,
    val nickname: String,
    val icon: String,
    val timeText: String,
    val messageText: String,
    val unreadText: String,
) {
    val hasUnread: Boolean
        get() = unreadText.isNotEmpty()
}
