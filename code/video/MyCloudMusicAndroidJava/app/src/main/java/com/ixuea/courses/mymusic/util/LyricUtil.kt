package com.ixuea.courses.mymusic.util

import com.ixuea.courses.mymusic.component.lyric.model.Line
import com.ixuea.courses.mymusic.component.lyric.model.Lyric

/**
 * 歌词相关
 */
object LyricUtil {
    /**
     * 计算当前播放时间是哪一行歌词。
     */
    @JvmStatic
    fun getLineNumber(data: Lyric, position: Int): Int {
        val datum = data.datum.orEmpty()
        for (i in datum.size - 1 downTo 0) {
            val line = datum[i]
            if (position >= line.startTime) {
                return i
            }
        }

        return 0
    }

    /**
     * 获取当前播放时间对应该行第几个字。
     */
    @JvmStatic
    fun getWordIndex(data: Line, progress: Long): Int {
        val words = data.words ?: return -1
        val wordDurations = data.wordDurations ?: return -1
        var startTime = data.startTime

        val count = minOf(words.size, wordDurations.size)
        for (i in 0 until count) {
            startTime += wordDurations[i].toLong()
            if (progress < startTime) {
                return i
            }
        }

        return -1
    }

    /**
     * 获取当前字播放的时间。
     */
    @JvmStatic
    fun getWordPlayedTime(data: Line, progress: Long): Float {
        val words = data.words ?: return -1f
        val wordDurations = data.wordDurations ?: return -1f
        var startTime = data.startTime

        val count = minOf(words.size, wordDurations.size)
        for (i in 0 until count) {
            startTime += wordDurations[i].toLong()
            if (progress < startTime) {
                return wordDurations[i] - (startTime - progress).toFloat()
            }
        }

        return -1f
    }

    /**
     * 获取当前时间对应的歌词行。
     */
    @JvmStatic
    fun getLyricLine(data: Lyric, progress: Long): Line? {
        val datum = data.datum.orEmpty()
        if (datum.isEmpty()) {
            return null
        }

        val lineNumber = getLineNumber(data, progress.toInt()).coerceIn(datum.indices)
        return datum[lineNumber]
    }
}
