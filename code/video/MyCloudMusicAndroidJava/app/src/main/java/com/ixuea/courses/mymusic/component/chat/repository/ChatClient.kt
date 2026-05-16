package com.ixuea.courses.mymusic.component.chat.repository

import android.net.Uri
import com.ixuea.courses.mymusic.AppContext
import com.ixuea.courses.mymusic.component.chat.model.MediaMessageExtra
import com.ixuea.courses.mymusic.util.ImageUtil
import com.ixuea.courses.mymusic.util.JSONUtil
import com.ixuea.courses.mymusic.util.MessageUtil
import io.rong.imlib.IRongCallback
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.Message
import io.rong.message.ImageMessage
import io.rong.message.TextMessage
import java.io.File
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Small RongCloud facade used by legacy Java screens while the chat chain moves
 * toward Repository/Flow state.
 */
class ChatClient private constructor() {
    interface Callback<T> {
        fun onSuccess(data: T)
        fun onError(errorCode: RongIMClient.ErrorCode?)
    }

    interface SendCallback {
        fun onAttached(message: Message)
        fun onSuccess(message: Message)
        fun onError(message: Message?, errorCode: RongIMClient.ErrorCode?)
    }

    interface ImageSendCallback : SendCallback {
        fun onProgress(message: Message, progress: Int)
    }

    private val incomingMessages = MutableSharedFlow<Message>(
        replay = 0,
        extraBufferCapacity = 64,
    )

    val messages: SharedFlow<Message> = incomingMessages.asSharedFlow()

    fun onMessageReceived(message: Message) {
        incomingMessages.tryEmit(message)
    }

    fun getConversationList(callback: Callback<List<Conversation>>) {
        client().getConversationList(object : RongIMClient.ResultCallback<List<Conversation>>() {
            override fun onSuccess(conversations: List<Conversation>?) {
                callback.onSuccess(conversations.orEmpty())
            }

            override fun onError(errorCode: RongIMClient.ErrorCode?) {
                callback.onError(errorCode)
            }
        })
    }

    fun deleteMessages(
        conversationType: Conversation.ConversationType,
        targetId: String,
        callback: Callback<Boolean>,
    ) {
        client().deleteMessages(
            conversationType,
            targetId,
            object : RongIMClient.ResultCallback<Boolean>() {
                override fun onSuccess(result: Boolean?) {
                    callback.onSuccess(result == true)
                }

                override fun onError(errorCode: RongIMClient.ErrorCode?) {
                    callback.onError(errorCode)
                }
            },
        )
    }

    fun clearUnread(
        conversationType: Conversation.ConversationType,
        targetId: String,
        callback: Callback<Boolean>,
    ) {
        client().clearMessagesUnreadStatus(
            conversationType,
            targetId,
            object : RongIMClient.ResultCallback<Boolean>() {
                override fun onSuccess(result: Boolean?) {
                    callback.onSuccess(result == true)
                }

                override fun onError(errorCode: RongIMClient.ErrorCode?) {
                    callback.onError(errorCode)
                }
            },
        )
    }

    fun getHistoryMessages(
        conversationType: Conversation.ConversationType,
        targetId: String,
        beforeMessageId: Int,
        count: Int,
        callback: Callback<List<Message>>,
    ) {
        client().getHistoryMessages(
            conversationType,
            targetId,
            beforeMessageId,
            count,
            object : RongIMClient.ResultCallback<List<Message>>() {
                override fun onSuccess(messages: List<Message>?) {
                    callback.onSuccess(messages.orEmpty().sortedBy { it.sentTime })
                }

                override fun onError(errorCode: RongIMClient.ErrorCode?) {
                    callback.onError(errorCode)
                }
            },
        )
    }

    fun sendText(
        targetId: String,
        content: String,
        senderUserId: String,
        callback: SendCallback,
    ) {
        val message = TextMessage.obtain(content)
        client().sendMessage(
            Conversation.ConversationType.PRIVATE,
            targetId,
            message,
            null,
            MessageUtil.createPushData(MessageUtil.getContent(message), senderUserId),
            object : IRongCallback.ISendMessageCallback {
                override fun onAttached(message: Message) {
                    callback.onAttached(message)
                }

                override fun onSuccess(message: Message) {
                    callback.onSuccess(message)
                }

                override fun onError(message: Message?, errorCode: RongIMClient.ErrorCode?) {
                    callback.onError(message, errorCode)
                }
            },
        )
    }

    fun sendImage(
        targetId: String,
        path: String,
        senderUserId: String,
        callback: ImageSendCallback,
    ) {
        val uri = Uri.fromFile(File(path))
        val message = ImageMessage.obtain(uri, false)
        val size = ImageUtil.getImageSize(path)
        message.extra = JSONUtil.toJSON(MediaMessageExtra(size[0], size[1]))

        client().sendImageMessage(
            Conversation.ConversationType.PRIVATE,
            targetId,
            message,
            null,
            MessageUtil.createPushData(MessageUtil.getContent(message), senderUserId),
            object : RongIMClient.SendImageMessageCallback() {
                override fun onAttached(message: Message) {
                    callback.onAttached(message)
                }

                override fun onError(message: Message?, errorCode: RongIMClient.ErrorCode?) {
                    callback.onError(message, errorCode)
                }

                override fun onSuccess(message: Message) {
                    callback.onSuccess(message)
                }

                override fun onProgress(message: Message, progress: Int) {
                    callback.onProgress(message, progress)
                }
            },
        )
    }

    fun markRead(message: Message) {
        message.receivedStatus.setRead()
        client().setMessageReceivedStatus(message.messageId, message.receivedStatus, null)
    }

    private fun client(): RongIMClient {
        return AppContext.getInstance().chatClient ?: RongIMClient.getInstance()
    }

    companion object {
        @JvmField
        val INSTANCE = ChatClient()
    }
}
