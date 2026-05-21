package com.ixuea.courses.mymusic.util

import io.rong.imlib.model.MessageContent
import io.rong.message.ImageMessage
import io.rong.message.TextMessage
import org.apache.commons.lang3.StringUtils

/**
 * 消息工具类
 */
object MessageUtil {
    @JvmStatic
    fun getContent(messageContent: MessageContent?): String {
        return when (messageContent) {
            is TextMessage -> messageContent.content
            is ImageMessage -> "[图片]"
            else -> ""
        }
    }

    @JvmStatic
    fun getNickname(id: String, nickname: String?): String {
        return if (StringUtils.isNotBlank(nickname)) nickname.orEmpty() else id
    }

    @JvmStatic
    @Suppress("UNUSED_PARAMETER")
    fun createPushData(_data: String, _userId: String): String {
        return ""
    }
}
