package com.ixuea.superui.util;

import android.view.Window;
import android.view.WindowManager;

/**
 * 状态栏工具类
 */
public class SuperStatusBarUtil {
    /**
     * 设置状态栏颜色
     *
     * @param window
     * @param color
     */
    public static void setStatusBarColor(Window window, int color) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);
    }
}
