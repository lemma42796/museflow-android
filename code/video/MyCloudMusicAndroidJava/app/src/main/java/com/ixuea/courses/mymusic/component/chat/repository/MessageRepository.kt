package com.ixuea.courses.mymusic.component.chat.repository

import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.Message

class MessageRepository(
    private val chatClient: ChatClient = ChatClient.INSTANCE,
) {
    val messages = chatClient.messages

    fun getHistoryMessages(
        targetId: String,
        beforeMessageId: Int,
        count: Int,
        callback: ChatClient.Callback<List<Message>>,
    ) {
        chatClient.getHistoryMessages(
            Conversation.ConversationType.PRIVATE,
            targetId,
            beforeMessageId,
            count,
            callback,
        )
    }

    fun sendText(
        targetId: String,
        content: String,
        senderUserId: String,
        callback: ChatClient.SendCallback,
    ) {
        chatClient.sendText(targetId, content, senderUserId, callback)
    }

    fun sendImage(
        targetId: String,
        path: String,
        senderUserId: String,
        callback: ChatClient.ImageSendCallback,
    ) {
        chatClient.sendImage(targetId, path, senderUserId, callback)
    }

    fun markRead(message: Message) {
        chatClient.markRead(message)
    }

    companion object {
        @JvmField
        val INSTANCE = MessageRepository()
    }
}
