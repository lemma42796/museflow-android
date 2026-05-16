package com.ixuea.courses.mymusic.component.conversation.model.event;


import io.rong.imlib.model.Message;

/**
 * 新消息事件
 */
public class NewMessageEvent {
    /**
     * 消息
     */
    private Message data;

    public NewMessageEvent(Message data) {
        this.data = data;
    }

    public Message getData() {
        return data;
    }

    public void setData(Message data) {
        this.data = data;
    }
}


