package com.ixuea.courses.mymusic.util

/**
 * 字符串相关工具类
 */
object StringUtil {
    private val sb = StringBuilder()

    /**
     * 格式化消息数量
     */
    @JvmStatic
    fun formatMessageCount(data: Int?): String {
        if (data!! > 99) {
            return "99+"
        }

        return data.toString()
    }

    /**
     * 是否符合密码格式
     */
    @JvmStatic
    fun isPassword(value: String): Boolean {
        return value.length in 6..15
    }

    /**
     * 是否符合昵称格式
     */
    @JvmStatic
    fun isNickname(value: String): Boolean {
        return value.length in 2..10
    }

    /**
     * 将一行字符串拆分为单个字
     */
    @JvmStatic
    fun words(data: String): Array<String> {
        val results = ArrayList<String>()

        val chars = data.toCharArray()
        for (c in chars) {
            results.add(c.toString())
        }

        return results.toTypedArray()
    }

    /**
     * 将一行英文字符串拆分为单个字
     *
     * @param data 例如：[I ][had ][a ][dream]
     */
    @JvmStatic
    fun englishWords(data: String): Array<String> {
        val results = ArrayList<String>()

        val chars = data.toCharArray()
        for (c in chars) {
            if (c == '[') {
                continue
            } else if (c == ']') {
                results.add(sb.toString())
                sb.setLength(0)
                continue
            }

            sb.append(c)
        }

        return results.toTypedArray()
    }

    /**
     * 是否是url
     */
    @JvmStatic
    fun isUrl(data: String): Boolean {
        return data.startsWith("http://") || data.startsWith("https://")
    }
}
