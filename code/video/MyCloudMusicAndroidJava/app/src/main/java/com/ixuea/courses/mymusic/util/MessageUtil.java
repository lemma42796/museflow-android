package com.ixuea.courses.mymusic.util;

import com.ixuea.courses.mymusic.component.push.model.Push;
import com.ixuea.courses.mymusic.component.push.model.PushMessage;

import org.apache.commons.lang3.StringUtils;

import io.rong.imlib.model.MessageContent;
import io.rong.message.ImageMessage;
import io.rong.message.TextMessage;

/**
 * 消息工具类
 */
public class MessageUtil {

    /**
     * 将不同的消息转为可文字描述的消息
     *
     * <p>用在通知栏，会话列表</p>
     *
     * @param messageContent
     * @return
     */
    public static String getContent(MessageContent messageContent) {
        if (messageContent instanceof TextMessage) {
            return ((TextMessage) messageContent).getContent();
        } else if (messageContent instanceof ImageMessage) {
            return "[图片]";
        }

        return "";
    }

    /**
     * 获取昵称
     *
     * @param id
     * @param nickname
     * @return
     */
    public static String getNickname(String id, String nickname) {
        return StringUtils.isNotBlank(nickname) ? nickname : id;
    }

    /**
     * 创建离线推送信息
     *
     * @param data
     * @param userId
     * @return
     */
    public static String createPushData(String data, String userId) {
        //创建推送消息
        PushMessage pushMessage = new PushMessage();
        pushMessage.setUserId(userId);
        pushMessage.setContent(data);

        Push result = new Push();
        result.setStyle(Push.PUSH_STYLE_CHAT);
        result.setMessage(pushMessage);

        //转为json
        return JSONUtil.toJSON(result);
    }
}
