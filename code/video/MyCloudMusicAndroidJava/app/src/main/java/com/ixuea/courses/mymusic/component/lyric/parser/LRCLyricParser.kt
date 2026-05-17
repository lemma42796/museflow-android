package com.ixuea.courses.mymusic.component.lyric.parser

import com.ixuea.courses.mymusic.component.lyric.model.Line
import com.ixuea.courses.mymusic.component.lyric.model.Lyric
import com.ixuea.courses.mymusic.util.SuperDateUtil

/**
 * LRC歌词解析器
 */
object LRCLyricParser {
    /**
     * 解析歌词
     */
    @JvmStatic
    fun parse(data: String?): Lyric {
        val result = Lyric()
        val datum = ArrayList<Line>()

        data.orEmpty()
            .split("\n")
            .mapNotNull(::parseLine)
            .forEach { line -> datum.add(line) }

        result.datum = datum
        return result
    }

    /**
     * 解析一行歌词
     * 例如：[00:00.300]爱的代价 - 李宗盛
     */
    private fun parseLine(data: String): Line? {
        if (!data.startsWith("[0")) {
            return null
        }

        val content = data.substring(1)
        val commands = content.split("]", limit = 2)
        if (commands.size < 2) {
            return null
        }

        return Line().apply {
            startTime = SuperDateUtil.parseToInt(commands[0]).toLong()
            this.data = commands[1]
        }
    }
}
