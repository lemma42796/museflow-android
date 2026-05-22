package com.ixuea.courses.mymusic.component.chat.repository

import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import kotlinx.coroutines.flow.SharedFlow

class ConversationRepository(
    private val chatClient: ChatClient = ChatClient.INSTANCE,
) {
    val unreadChanged: SharedFlow<Unit> = chatClient.unreadChanged

    fun getConversationList(callback: ChatClient.Callback<List<Conversation>>) {
        chatClient.getConversationList(callback)
    }

    fun deleteMessages(
        conversationType: Conversation.ConversationType,
        targetId: String,
        callback: ChatClient.Callback<Boolean>,
    ) {
        chatClient.deleteMessages(conversationType, targetId, callback)
    }

    fun clearUnread(
        targetId: String,
        callback: ChatClient.Callback<Boolean>,
    ) {
        chatClient.clearUnread(Conversation.ConversationType.PRIVATE, targetId, callback)
    }

    companion object {
        @JvmField
        val INSTANCE = ConversationRepository()
    }
}
