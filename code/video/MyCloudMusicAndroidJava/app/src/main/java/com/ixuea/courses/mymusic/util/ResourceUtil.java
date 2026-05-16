package com.ixuea.courses.mymusic.util;

import android.content.Context;
import android.content.res.TypedArray;

import com.ixuea.courses.mymusic.config.Config;

/**
 * 资源工具类
 */
public class ResourceUtil {
    /**
     * 将相对资源转为绝对路径
     *
     * @param data 放到自己服务端的资源以files开头，其他资源都在阿里云oss
     * @return
     */
    public static String resourceUri(String data) {
        return String.format(Config.RESOURCE_ENDPOINT, data);
    }

    /**
     * 获取颜色属性值
     */
    public static int getColorAttributes(Context context, int data) {
        TypedArray typedArray = context.obtainStyledAttributes(new int[]{data});
        return typedArray.getColor(0, 0);
    }
}
