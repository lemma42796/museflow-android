package com.ixuea.courses.mymusic.component.me.model.ui;

import com.ixuea.courses.mymusic.component.sheet.model.Sheet;

import java.util.List;

/**
 * 首页-我的界面分组模型
 */
public class MeGroup {
    /**
     * 标题
     */
    private int title;

    /**
     * 数据
     */
    private List<Sheet> datum;

    /**
     * 是否显示右侧按钮
     */
    private boolean isMore;

    public MeGroup(int title, List<Sheet> datum, boolean isMore) {
        this.title = title;
        this.datum = datum;
        this.isMore = isMore;
    }

    public int getTitle() {
        return title;
    }

    public void setTitle(int title) {
        this.title = title;
    }

    public List<Sheet> getDatum() {
        return datum;
    }

    public void setDatum(List<Sheet> datum) {
        this.datum = datum;
    }

    public boolean isMore() {
        return isMore;
    }

    public void setMore(boolean more) {
        isMore = more;
    }
}
