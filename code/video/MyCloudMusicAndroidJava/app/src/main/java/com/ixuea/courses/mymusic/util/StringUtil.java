package com.ixuea.courses.mymusic.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 字符串相关工具类
 */
public class StringUtil {
    private static StringBuilder sb = new StringBuilder();

    /**
     * 格式化消息数量
     *
     * @param data
     * @return
     */
    public static String formatMessageCount(Integer data) {
        if (data > 99) {
            return "99+";
        }

        return String.valueOf(data);
    }

    /**
     * 是否符合密码格式
     *
     * @param value
     * @return
     */
    public static boolean isPassword(String value) {
        return value.length() >= 6 && value.length() <= 15;
    }

    /**
     * 是否符合昵称格式
     *
     * @param value
     * @return
     */
    public static boolean isNickname(String value) {
        return value.length() >= 2 && value.length() <= 10;
    }

    /**
     * 将一行字符串
     * 拆分为单个字
     *
     * @param data
     * @return
     */
    public static String[] words(String data) {
        //创建一个列表
        List<String> results = new ArrayList<>();

        //转为char数组
        char[] chars = data.toCharArray();

        //循环每一个字符
        for (char c : chars
        ) {
            //转为字符串
            //添加到列表
            results.add(String.valueOf(c));
        }

        //转为数组
        return results.toArray(new String[results.size()]);
    }

    /**
     * 将一行英文字符串
     * 拆分为单个字
     *
     * @param data 例如：[I ][had ][a ][dream]
     * @return
     */
    public static String[] englishWords(String data) {
        //创建一个列表
        List<String> results = new ArrayList<>();

        //转为char数组
        char[] chars = data.toCharArray();

        //循环每一个字符
        for (char c : chars) {
            if (c == '[') {
                continue;
            } else if (c == ']') {
                results.add(sb.toString());
                sb.setLength(0);
                continue;
            }

            sb.append(c);
        }

        //转为数组
        return results.toArray(new String[results.size()]);
    }

    /**
     * 是否是url
     */
    public static boolean isUrl(String data) {
        return data.startsWith("http://") || data.startsWith("https://");
    }
}
