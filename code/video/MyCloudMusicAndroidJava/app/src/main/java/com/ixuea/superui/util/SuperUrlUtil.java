package com.ixuea.superui.util;

import java.util.HashMap;
import java.util.Map;

/**
 * url工具类
 */
public class SuperUrlUtil {
    /**
     * 获取网址里面的查询参数
     *
     * @param data
     * @return
     */
    public static Map<String, Object> getQueryMap(String data) {
        Map<String, Object> map = new HashMap<>();

        data = data.replace("?", ";");
        if (!data.contains(";")) {
            return map;
        }
        if (data.split(";").length > 0) {
            String[] arr = data.split(";")[1].split("&");
            for (String s : arr) {
                String key = s.split("=")[0];
                String value = s.split("=")[1];
                map.put(key, value);
            }
            return map;

        } else {
            return map;
        }
    }
}
