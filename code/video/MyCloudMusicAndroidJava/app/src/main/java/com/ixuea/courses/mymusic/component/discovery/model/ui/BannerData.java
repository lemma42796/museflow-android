package com.ixuea.courses.mymusic.component.discovery.model.ui;

import com.ixuea.courses.mymusic.component.ad.model.Ad;
import com.ixuea.courses.mymusic.model.ui.BaseMultiItemEntity;
import com.ixuea.courses.mymusic.util.Constant;

import java.util.List;

/**
 * 发现界面，轮播图数据
 */
public class BannerData extends BaseSort implements BaseMultiItemEntity {
    private List<Ad> data;

    public BannerData(List<Ad> data, int sort) {
        super(sort);
        this.data = data;
    }

    public List<Ad> getData() {
        return data;
    }

    public void setData(List<Ad> data) {
        this.data = data;
    }

    @Override
    public int getItemType() {
        return Constant.STYLE_BANNER;
    }
}
