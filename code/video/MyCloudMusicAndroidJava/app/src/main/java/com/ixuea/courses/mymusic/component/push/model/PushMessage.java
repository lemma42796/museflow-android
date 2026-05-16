package com.ixuea.courses.mymusic.component.push.model;

import com.ixuea.courses.mymusic.model.Base;

/**
 * 离线推送消息对象
 */
public class PushMessage extends Base {
    /**
     * 发送者用户id
     */
    private String userId;

    /**
     * 内容
     * 文本就是消息
     * 其他消息，格式化后的文本，例如：图片就是[图片]
     */
    private String content;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
