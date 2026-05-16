package com.ixuea.courses.mymusic.component.user.model.event;

public class UserDetailEvent {
    private final String data;

    public UserDetailEvent(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }
}
