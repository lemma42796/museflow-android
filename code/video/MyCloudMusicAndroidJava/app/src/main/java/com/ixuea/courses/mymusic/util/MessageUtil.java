package com.ixuea.courses.mymusic.util;

import org.apache.commons.lang3.StringUtils;

import io.rong.imlib.model.MessageContent;
import io.rong.message.ImageMessage;
import io.rong.message.TextMessage;

/**
 * 消息工具类
 */
public class MessageUtil {
    public static String getContent(MessageContent messageContent) {
        if (messageContent instanceof TextMessage) {
            return ((TextMessage) messageContent).getContent();
        } else if (messageContent instanceof ImageMessage) {
            return "[图片]";
        }

        return "";
    }

    public static String getNickname(String id, String nickname) {
        return StringUtils.isNotBlank(nickname) ? nickname : id;
    }

    public static String createPushData(String data, String userId) {
        return "";
    }
}
