package com.ixuea.courses.mymusic.util;

/**
 * 对数据加盐
 */
public class SaltUtil {
    /**
     * 加盐
     *
     * @param data
     * @return
     */
    public static String wrap(String data) {
        return String.format("%s%s%s", Constant.SALT_START, data, Constant.SALT_END);
    }

    public static String unwrap(String data) {
        return data.replace(Constant.SALT_START, "").replace(Constant.SALT_END, "");
    }
}
