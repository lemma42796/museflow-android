package com.ixuea.courses.mymusic.component.address.model.request;

import com.ixuea.courses.mymusic.model.Base;

/**
 * 只传递一个data文本字段请求对象
 */
public class DataRequest extends Base {
    private String data;

    public DataRequest() {
    }

    public DataRequest(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
