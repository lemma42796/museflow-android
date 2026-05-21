package com.ixuea.courses.mymusic.util

import org.joda.time.DateTime
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

/**
 * 日期时间工具类
 */
object SuperDateUtil {
    private const val ONE_MINUTE = 60000L
    private const val ONE_HOUR = 3600000L
    private const val ONE_DAY = 86400000L
    private const val PATTERN_YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm"
    private const val PATTERN_YYYY_MM_DD = "yyyy-MM-dd"
    private const val PATTERN_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss"

    /**
     * 当前年。
     */
    @JvmStatic
    fun currentYear(): Int {
        return Calendar.getInstance().get(Calendar.YEAR)
    }

    /**
     * 当前天。
     */
    @JvmStatic
    fun currentDay(): Int {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    }

    /**
     * 将毫秒格式化为分:秒，例如：150:11。
     */
    @JvmStatic
    fun ms2ms(data: Int): String {
        if (data == 0) {
            return "00:00"
        }

        return s2ms(data / 1000)
    }

    /**
     * 将秒格式化为分:秒，例如：150:11。
     */
    @JvmStatic
    fun s2ms(data: Int): String {
        if (data == 0) {
            return "00:00"
        }

        val minute = data / 60
        val second = data - minute * 60
        return String.format("%02d:%02d", minute, second)
    }

    /**
     * 将分秒毫秒数据转为毫秒，格式为：00:06.429。
     */
    @JvmStatic
    fun parseToInt(data: String): Long {
        val strings = data.replace(":", ".").split("\\.".toRegex())
        val minute = strings[0].toInt()
        val second = strings[1].toInt()
        val ms = strings[2].toInt()
        return (minute * 60 + second) * 1000L + ms
    }

    /**
     * 将 ISO8601 字符串转为项目中通用的格式。
     */
    @JvmStatic
    fun commonFormat(date: String?): String {
        return commonFormat(DateTime(date))
    }

    /**
     * 将时间戳转为项目中通用的格式。
     */
    @JvmStatic
    fun commonFormat(data: Long): String {
        return commonFormat(DateTime(data))
    }

    private fun commonFormat(dateTime: DateTime): String {
        val value = Date().time - dateTime.toDate().time

        return when {
            value < ONE_MINUTE -> {
                val data = toSeconds(value)
                String.format("%d秒前", if (data <= 0) 1 else data)
            }

            value < 60 * ONE_MINUTE -> String.format("%d分钟前", toMinutes(value))
            value < 24 * ONE_HOUR -> String.format("%d小时前", toHours(value))
            value < 30 * ONE_DAY -> String.format("%d天前", toDays(value))
            else -> yyyyMMdd(dateTime)
        }
    }

    /**
     * 将 ISO8601 字符串转为 yyyy-MM-dd HH:mm。
     */
    @JvmStatic
    fun yyyyMMdd(date: String): String {
        return yyyyMMdd(DateTime(date))
    }

    /**
     * 将 DateTime 转为 yyyy-MM-dd HH:mm。
     */
    @JvmStatic
    fun yyyyMMdd(dateTime: DateTime): String {
        return dateTime.toString(PATTERN_YYYY_MM_DD_HH_MM)
    }

    /**
     * 时间戳转为 yyyy-MM-dd。
     */
    @JvmStatic
    fun yyyyMMdd(data: Long): String {
        val formatter = SimpleDateFormat(PATTERN_YYYY_MM_DD)
        return formatter.format(data)
    }

    /**
     * 当前日期转为 yyyy-MM-dd HH:mm:ss。
     */
    @JvmStatic
    fun nowyyyyMMddHHmmss(): String {
        return DateTime().toString(PATTERN_YYYY_MM_DD_HH_MM_SS)
    }

    /**
     * 将 ISO8601 字符串转为 yyyy-MM-dd HH:mm:ss。
     */
    @JvmStatic
    fun yyyyMMddHHmmss(data: String): String {
        return DateTime(data).toString(PATTERN_YYYY_MM_DD_HH_MM_SS)
    }

    /**
     * 将毫秒格式化为时:分:秒，例如：10:20:11。
     */
    @JvmStatic
    fun ms2hms(data: Int): String {
        if (data == 0) {
            return "00:00:00"
        }

        return SimpleDateFormat("HH:mm:ss").apply {
            timeZone = TimeZone.getTimeZone("GMT+00:00")
        }.format(data)
    }

    private fun toSeconds(date: Long): Long = date / 1000L

    private fun toMinutes(date: Long): Long = toSeconds(date) / 60L

    private fun toHours(date: Long): Long = toMinutes(date) / 60L

    private fun toDays(date: Long): Long = toHours(date) / 24L
}
