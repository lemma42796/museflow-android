package com.ixuea.courses.mymusic.component.push.model;

import com.ixuea.courses.mymusic.model.Base;

/**
 * 推送模型
 */
public class Push extends Base {
    /**
     * 聊天消息
     */
    public static final int PUSH_STYLE_CHAT = 10;

    /**
     * 推送类型
     */
    private int style;

    private String id;

    /**
     * 聊天推送消息
     */
    private PushMessage message;

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PushMessage getMessage() {
        return message;
    }

    public void setMessage(PushMessage message) {
        this.message = message;
    }
}

