package com.ixuea.courses.mymusic.component.search.model.event;

/**
 * 搜索事件
 */
public class SearchEvent {
    /**
     * 搜索关键字
     */
    private String data;

    /**
     * 当前显示界面的索引
     */
    private int selectedIndex;

    public SearchEvent(String data, int selectedIndex) {
        this.data = data;
        this.selectedIndex = selectedIndex;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }
}
