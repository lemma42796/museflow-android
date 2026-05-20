package com.ixuea.courses.mymusic.util

/**
 * 列表工具类
 */
object ListUtil {
    /**
     * 遍历每一个接口
     */
    @JvmStatic
    fun <T> eachListener(datum: List<T>, action: Consumer<T>) {
        for (listener in datum) {
            action.accept(listener)
        }
    }

    /**
     * 消费者接口。
     */
    fun interface Consumer<T> {
        fun accept(t: T)
    }
}
