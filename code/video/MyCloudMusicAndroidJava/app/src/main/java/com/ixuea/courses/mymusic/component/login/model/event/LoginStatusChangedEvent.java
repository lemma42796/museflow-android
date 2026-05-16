package com.ixuea.courses.mymusic.component.login.model.event;

/**
 * 登录状态改变了事件
 */
public class LoginStatusChangedEvent {
    /**
     * 是否登录了
     */
    private boolean isLogin;

    public LoginStatusChangedEvent(boolean isLogin) {
        this.isLogin = isLogin;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }
}
