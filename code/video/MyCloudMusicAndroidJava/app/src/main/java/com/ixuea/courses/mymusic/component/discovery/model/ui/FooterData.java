package com.ixuea.courses.mymusic.component.discovery.model.ui;

import com.ixuea.courses.mymusic.model.ui.BaseMultiItemEntity;
import com.ixuea.courses.mymusic.util.Constant;

/**
 * 发现界面，尾部数据
 */
public class FooterData extends BaseSort implements BaseMultiItemEntity {
    public FooterData() {
        //永远排序到最后，设置设置一个很大值
        super(Integer.MAX_VALUE);
    }

    @Override
    public int getItemType() {
        return Constant.STYLE_FOOTER;
    }
}
