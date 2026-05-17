package com.ixuea.courses.mymusic.component.lyric.parser

import com.ixuea.courses.mymusic.component.lyric.model.Line
import com.ixuea.courses.mymusic.component.lyric.model.Lyric
import com.ixuea.courses.mymusic.util.StringUtil
import com.ixuea.courses.mymusic.util.SuperDateUtil

/**
 * KSC歌词解析器
 */
object KSCLyricParser {
    /**
     * 解析歌词
     */
    @JvmStatic
    fun parse(data: String?): Lyric {
        val result = Lyric()
        result.isAccurate = true

        val datum = ArrayList<Line>()
        data.orEmpty()
            .split(";")
            .mapNotNull { lineString -> parseLine(lineString.trim()) }
            .forEach { line -> datum.add(line) }

        result.datum = datum
        return result
    }

    /**
     * 解析每一行歌词
     *
     * 例如中文：karaoke.add('00:27.487', '00:32.068', '一时失志不免怨叹', '347,373,1077,320,344,386,638,1096');
     * 英文：   karaoke.add('00:48.153', '00:49.234', '[I ][had ][a ][dream]', '185,200,191,500');
     */
    private fun parseLine(data: String): Line? {
        if (!data.startsWith("karaoke.add") || data.length <= KARAOKE_PREFIX_LENGTH + KARAOKE_SUFFIX_LENGTH) {
            return null
        }

        val content = data.substring(KARAOKE_PREFIX_LENGTH, data.length - KARAOKE_SUFFIX_LENGTH)
        val commands = content.split("', '", limit = 4)
        if (commands.size < 4) {
            return null
        }

        return Line().apply {
            startTime = SuperDateUtil.parseToInt(commands[0]).toLong()
            endTime = SuperDateUtil.parseToInt(commands[1]).toLong()

            val command = commands[2]
            if (command.startsWith("[")) {
                words = StringUtil.englishWords(command)
                this.data = words?.joinToString(" ").orEmpty()
            } else {
                words = StringUtil.words(command)
                this.data = command
            }

            wordDurations = commands[3]
                .split(",")
                .mapNotNull { value -> value.toIntOrNull() }
                .toIntArray()
        }
    }

    private const val KARAOKE_PREFIX_LENGTH = 13
    private const val KARAOKE_SUFFIX_LENGTH = 3
}
