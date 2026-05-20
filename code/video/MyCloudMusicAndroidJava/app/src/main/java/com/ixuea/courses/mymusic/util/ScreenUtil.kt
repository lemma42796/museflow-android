package com.ixuea.courses.mymusic.util

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager

/**
 * 屏幕工具类
 */
object ScreenUtil {
    /**
     * 获取屏幕宽度
     */
    @JvmStatic
    @Suppress("DEPRECATION")
    fun getScreenWith(context: Context): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val outDisplayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(outDisplayMetrics)
        return outDisplayMetrics.widthPixels
    }

    /**
     * 获取屏幕高度
     */
    @JvmStatic
    @Suppress("DEPRECATION")
    fun getScreenHeight(context: Context): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val outDisplayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(outDisplayMetrics)
        return outDisplayMetrics.heightPixels
    }
}
