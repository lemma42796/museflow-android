package com.ixuea.courses.mymusic.component.discovery.model.ui;

/**
 * 发现界面Item
 */
public class CustomDiscoveryItem extends BaseSort {
    private int style;
    private int title;

    public CustomDiscoveryItem(int style, int title, int sort) {
        super(sort);
        this.style = style;
        this.title = title;
    }

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public int getTitle() {
        return title;
    }

    public void setTitle(int title) {
        this.title = title;
    }
}
