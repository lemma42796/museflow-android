package com.ixuea.courses.mymusic.util;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * JSON工具类
 */
public class JSONUtil {
    public static Gson createGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();

        //驼峰转下划线
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        Gson gson = gsonBuilder.create();

        return gson;
    }

    public static String toJSON(Object data) {
        return createGson().toJson(data);
    }

    /**
     * 将Json转为对象
     *
     * @param data
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T fromJSON(String data, Class<T> clazz) {
        //通过gson转换
        return createGson()
                .fromJson(data, clazz);
    }
}
