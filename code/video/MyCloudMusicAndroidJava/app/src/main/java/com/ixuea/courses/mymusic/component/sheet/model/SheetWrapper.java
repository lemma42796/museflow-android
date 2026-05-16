package com.ixuea.courses.mymusic.component.sheet.model;

import com.ixuea.courses.mymusic.model.response.Meta;

/**
 * 用来解析歌单列表数据
 */
public class SheetWrapper {
    /**
     * 状态码
     * 等于0表示成功
     */
    private int status;

    /**
     * 出错的提示信息
     * 发生了错误不一定有
     */
    private String message;

    private Meta data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Meta getData() {
        return data;
    }

    public void setData(Meta data) {
        this.data = data;
    }
}
