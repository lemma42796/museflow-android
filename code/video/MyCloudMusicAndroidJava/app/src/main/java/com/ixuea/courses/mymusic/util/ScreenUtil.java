package com.ixuea.courses.mymusic.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * 屏幕工具类
 */
public class ScreenUtil {
    /**
     * 获取屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getScreenWith(Context context) {
        //获取window管理器
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        //创建显示对象
        DisplayMetrics outDisplayMetrics = new DisplayMetrics();

        //获取默认显示对象
        wm.getDefaultDisplay().getMetrics(outDisplayMetrics);

        //返回屏幕宽度
        return outDisplayMetrics.widthPixels;
    }

    /**
     * 获取屏幕高度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        //获取window管理器
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        //创建显示对象
        DisplayMetrics outDisplayMetrics = new DisplayMetrics();

        //获取默认显示对象
        wm.getDefaultDisplay().getMetrics(outDisplayMetrics);

        //返回屏幕宽度
        return outDisplayMetrics.heightPixels;
    }
}
