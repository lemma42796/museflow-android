package com.ixuea.courses.mymusic.util;

import android.util.Base64;

/**
 * Android Base64工具类
 * 使用Android中自带的工具类实现
 */
public class Base64Util {
    /**
     * 编码
     *
     * @param data
     * @return
     */
    public static String encodeString2String(String data) {
        return Base64.encodeToString(data.getBytes(), Base64.NO_WRAP);
    }

    /**
     * 编码
     *
     * @param data
     * @return
     */
    public static byte[] encodeByte2Byte(byte[] data) {
        return Base64.encode(data, Base64.NO_WRAP);
    }

    /**
     * 编码
     *
     * @param data
     * @return
     */
    public static String encodeByte2String(byte[] data) {
        return Base64.encodeToString(data, Base64.NO_WRAP);
    }

    /**
     * 解码
     *
     * @param data
     * @return
     */
    public static String decodeString2String(String data) {
        return new String(decodeByte2Byte(data.getBytes()));
    }

    /**
     * 解码
     *
     * @param data
     * @return
     */
    public static byte[] decodeByte2Byte(byte[] data) {
        return Base64.decode(data, Base64.NO_WRAP);
    }

    /**
     * 解码
     *
     * @param data
     * @return
     */
    public static byte[] decodeString2Byte(String data) {
        return Base64.decode(data.getBytes(), Base64.NO_WRAP);
    }
}

