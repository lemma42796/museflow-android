package com.ixuea.courses.mymusic.util

import android.content.Context
import com.ixuea.courses.mymusic.config.Config

/**
 * 资源工具类
 */
object ResourceUtil {
    /**
     * 将相对资源转为绝对路径
     *
     * @param data 放到自己服务端的资源以files开头，其他资源都在阿里云oss
     */
    @JvmStatic
    fun resourceUri(data: String?): String {
        return String.format(Config.RESOURCE_ENDPOINT, data)
    }

    /**
     * 获取颜色属性值
     */
    @JvmStatic
    fun getColorAttributes(context: Context, data: Int): Int {
        val typedArray = context.obtainStyledAttributes(intArrayOf(data))
        return try {
            typedArray.getColor(0, 0)
        } finally {
            typedArray.recycle()
        }
    }
}
