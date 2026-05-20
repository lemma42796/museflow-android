package com.ixuea.courses.mymusic.util

import android.graphics.Paint
import kotlin.math.ceil

object TextUtil {
    /**
     * 获取文本的宽度
     */
    @JvmStatic
    fun getTextWidth(paint: Paint, data: String): Float {
        return paint.measureText(data)
    }

    /**
     * 获取文本的高度
     */
    @JvmStatic
    fun getTextHeight(paint: Paint): Float {
        val fontMetrics = paint.fontMetrics
        return ceil((fontMetrics.descent - fontMetrics.ascent).toDouble()).toFloat()
    }
}
