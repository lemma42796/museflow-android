package com.ixuea.courses.mymusic.util

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder

/**
 * JSON工具类
 */
object JSONUtil {
    @JvmStatic
    fun createGson(): Gson {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        return gsonBuilder.create()
    }

    @JvmStatic
    fun toJSON(data: Any?): String {
        return createGson().toJson(data)
    }

    /**
     * 将Json转为对象
     */
    @JvmStatic
    fun <T> fromJSON(data: String?, clazz: Class<T>): T {
        return createGson().fromJson(data, clazz)
    }
}
