package com.ixuea.courses.mymusic.component.location.model.event;

/**
 * 选择了位置事件
 */
public class SelectLocationEvent {
    private Object data;

    public SelectLocationEvent(Object data) {
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
