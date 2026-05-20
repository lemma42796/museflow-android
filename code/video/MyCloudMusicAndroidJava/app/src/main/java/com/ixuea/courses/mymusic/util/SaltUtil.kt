package com.ixuea.courses.mymusic.util

/**
 * 对数据加盐
 */
object SaltUtil {
    /**
     * 加盐
     */
    @JvmStatic
    fun wrap(data: String): String {
        return String.format("%s%s%s", Constant.SALT_START, data, Constant.SALT_END)
    }

    @JvmStatic
    fun unwrap(data: String): String {
        return data.replace(Constant.SALT_START, "").replace(Constant.SALT_END, "")
    }
}
