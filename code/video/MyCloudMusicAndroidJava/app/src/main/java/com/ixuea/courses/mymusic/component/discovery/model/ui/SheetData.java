package com.ixuea.courses.mymusic.component.discovery.model.ui;

import com.ixuea.courses.mymusic.component.sheet.model.Sheet;
import com.ixuea.courses.mymusic.model.ui.BaseMultiItemEntity;
import com.ixuea.courses.mymusic.util.Constant;

import java.util.List;

/**
 * 发现界面，歌单外层容器数据
 */
public class SheetData extends BaseSort implements BaseMultiItemEntity {

    private List<Sheet> data;

    public SheetData(List<Sheet> data, int sort) {
        super(sort);
        this.data = data;
    }

    public List<Sheet> getData() {
        return data;
    }

    public void setData(List<Sheet> data) {
        this.data = data;
    }

    @Override
    public int getItemType() {
        return Constant.STYLE_SHEET;
    }
}
