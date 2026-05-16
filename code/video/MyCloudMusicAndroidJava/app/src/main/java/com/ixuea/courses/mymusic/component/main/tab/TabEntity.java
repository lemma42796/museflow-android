package com.ixuea.courses.mymusic.component.main.tab;

import com.flyco.tablayout.listener.CustomTabEntity;

/**
 * 自定义指示器模型
 */
public class TabEntity implements CustomTabEntity {
    /**
     * 标题
     */
    public String title;

    /**
     * 选中的图标
     */
    public int selectedIcon;

    /**
     * 未选中图标
     */
    public int unSelectedIcon;

    public TabEntity(String title, int selectedIcon, int unSelectedIcon) {
        this.title = title;
        this.selectedIcon = selectedIcon;
        this.unSelectedIcon = unSelectedIcon;
    }

    @Override
    public String getTabTitle() {
        return title;
    }

    @Override
    public int getTabSelectedIcon() {
        return selectedIcon;
    }

    @Override
    public int getTabUnselectedIcon() {
        return unSelectedIcon;
    }
}
