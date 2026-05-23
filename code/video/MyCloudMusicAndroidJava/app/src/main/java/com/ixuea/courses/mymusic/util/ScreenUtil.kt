package com.ixuea.courses.mymusic.util

import android.content.Context

/**
 * 屏幕工具类
 */
object ScreenUtil {
    /**
     * 获取屏幕宽度
     */
    @JvmStatic
    fun getScreenWith(context: Context): Int {
        return context.resources.displayMetrics.widthPixels
    }

    /**
     * 获取屏幕高度
     */
    @JvmStatic
    fun getScreenHeight(context: Context): Int {
        return context.resources.displayMetrics.heightPixels
    }
}
