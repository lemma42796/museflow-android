package com.ixuea.courses.mymusic.util

import android.content.Context

/**
 * 尺寸相关工具栏
 */
object SizeUtil {
    /**
     * 状态栏高度
     */
    private var statusBarHeight = 0

    /**
     * 获取状态栏高度
     */
    @JvmStatic
    fun getStatusBarHeight(context: Context): Int {
        if (statusBarHeight == 0) {
            try {
                val clazz = Class.forName("com.android.internal.R\$dimen")
                val instance = clazz.getDeclaredConstructor().newInstance()
                val field = clazz.getField("status_bar_height")
                val height = field.get(instance) as Int
                statusBarHeight = context.resources.getDimensionPixelSize(height)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return statusBarHeight
    }
}
