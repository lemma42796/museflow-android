package com.ixuea.superui.util;

import java.util.UUID;

/**
 * uuid工具类
 */
public class UUIDUtil {
    /**
     * 获取uuid
     *
     * @return
     */
    public static String getUUID() {
        String uuid = UUID.randomUUID().toString();
        //去掉-
        return uuid.replace("-", "");
    }
}