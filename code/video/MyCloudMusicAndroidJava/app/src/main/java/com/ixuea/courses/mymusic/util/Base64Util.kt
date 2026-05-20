package com.ixuea.courses.mymusic.util

import android.util.Base64
import java.nio.charset.Charset

/**
 * Android Base64工具类，使用Android中自带的工具类实现。
 */
object Base64Util {
    private val defaultCharset: Charset
        get() = Charset.defaultCharset()

    /**
     * 编码
     */
    @JvmStatic
    fun encodeString2String(data: String): String {
        return Base64.encodeToString(data.toByteArray(defaultCharset), Base64.NO_WRAP)
    }

    /**
     * 编码
     */
    @JvmStatic
    fun encodeByte2Byte(data: ByteArray): ByteArray {
        return Base64.encode(data, Base64.NO_WRAP)
    }

    /**
     * 编码
     */
    @JvmStatic
    fun encodeByte2String(data: ByteArray): String {
        return Base64.encodeToString(data, Base64.NO_WRAP)
    }

    /**
     * 解码
     */
    @JvmStatic
    fun decodeString2String(data: String): String {
        return String(decodeByte2Byte(data.toByteArray(defaultCharset)), defaultCharset)
    }

    /**
     * 解码
     */
    @JvmStatic
    fun decodeByte2Byte(data: ByteArray): ByteArray {
        return Base64.decode(data, Base64.NO_WRAP)
    }

    /**
     * 解码
     */
    @JvmStatic
    fun decodeString2Byte(data: String): ByteArray {
        return Base64.decode(data.toByteArray(defaultCharset), Base64.NO_WRAP)
    }
}
