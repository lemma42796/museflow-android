package com.ixuea.courses.mymusic.component.chat.model;

import com.ixuea.courses.mymusic.model.Base;

public class MediaMessageExtra extends Base {
    private int width;
    private int height;

    public MediaMessageExtra() {
    }

    public MediaMessageExtra(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
