package com.ixuea.courses.mymusic.component.user.model.ui;

import com.ixuea.courses.mymusic.model.ui.BaseMultiItemEntity;
import com.ixuea.courses.mymusic.util.Constant;

public class TitleData implements BaseMultiItemEntity {
    private int title;
    private String titleString;

    public TitleData(int title) {
        this.title = title;
    }

    public TitleData(String titleString) {
        this.titleString = titleString;
    }

    public String getTitleString() {
        return titleString;
    }

    public void setTitleString(String titleString) {
        this.titleString = titleString;
    }

    public int getTitle() {
        return title;
    }

    public void setTitle(int title) {
        this.title = title;
    }

    @Override
    public int getItemType() {
        return Constant.STYLE_TITLE;
    }
}
