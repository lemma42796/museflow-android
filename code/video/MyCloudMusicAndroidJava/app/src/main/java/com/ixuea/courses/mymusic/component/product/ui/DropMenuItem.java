package com.ixuea.courses.mymusic.component.product.ui;

/**
 * 下拉菜单item模型
 */
public class DropMenuItem {
    private int title;
    private int value;

    public DropMenuItem(int title, int value) {
        this.title = title;
        this.value = value;
    }

    public int getTitle() {
        return title;
    }

    public void setTitle(int title) {
        this.title = title;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
