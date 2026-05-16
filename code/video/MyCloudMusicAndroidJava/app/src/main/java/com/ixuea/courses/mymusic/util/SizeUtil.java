package com.ixuea.courses.mymusic.util;

import android.content.Context;

import java.lang.reflect.Field;

/**
 * 尺寸相关工具栏
 */
public class SizeUtil {
    /**
     * 状态栏高度
     */
    private static int statusBarHeight;

    /**
     * 获取状态栏高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        if (statusBarHeight == 0) {
            try {
                //获取某一个类的class
                Class<?> c = Class.forName("com.android.internal.R$dimen");

                //创建一个对象
                Object o = c.newInstance();

                //获取状态栏这个字段
                Field field = c.getField("status_bar_height");

                //获取状态栏这个字段的值
                int height = (Integer) field.get(o);

                //将值转为px
                statusBarHeight = context.getResources().getDimensionPixelSize(height);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return statusBarHeight;
    }
}
