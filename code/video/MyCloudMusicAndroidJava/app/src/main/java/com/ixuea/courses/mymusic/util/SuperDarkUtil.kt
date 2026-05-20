package com.ixuea.courses.mymusic.util

import android.content.Context
import android.content.res.Configuration

/**
 * 深色模型工具类
 */
object SuperDarkUtil {
    /**
     * 是否是深色模型
     */
    @JvmStatic
    fun isDark(context: Context): Boolean {
        return (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
            Configuration.UI_MODE_NIGHT_YES
    }
}
