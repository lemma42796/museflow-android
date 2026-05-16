package com.ixuea.courses.mymusic.component.user.model.event;

import com.ixuea.courses.mymusic.component.user.model.User;

/**
 * 选择了好友
 */
public class SelectedFriendEvent {
    private User data;

    public SelectedFriendEvent(User data) {
        this.data = data;
    }

    public User getData() {
        return data;
    }

    public void setData(User data) {
        this.data = data;
    }
}
