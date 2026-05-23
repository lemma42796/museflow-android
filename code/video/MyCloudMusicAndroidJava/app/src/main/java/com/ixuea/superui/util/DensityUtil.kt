package com.ixuea.superui.util

import android.content.Context

/**
 * Android 尺寸相关工具。
 */
object DensityUtil {
    @JvmStatic
    fun dip2px(context: Context, data: Float): Float {
        val scale = context.resources.displayMetrics.density
        return (data * scale + 0.5f).toInt().toFloat()
    }
}
