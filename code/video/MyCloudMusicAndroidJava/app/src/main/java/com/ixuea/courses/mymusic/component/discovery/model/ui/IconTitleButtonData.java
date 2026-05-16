package com.ixuea.courses.mymusic.component.discovery.model.ui;

/**
 * 图标文本按钮模型
 */
public class IconTitleButtonData {
    private int icon;
    private int title;

    public IconTitleButtonData(int icon, int title) {
        this.icon = icon;
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getTitle() {
        return title;
    }

    public void setTitle(int title) {
        this.title = title;
    }
}
