package com.ixuea.courses.mymusic.component.lyric.parser

import com.ixuea.courses.mymusic.component.lyric.model.Lyric
import com.ixuea.courses.mymusic.util.Constant

/**
 * 歌词解析器
 */
object LyricParser {
    /**
     * 解析歌词
     */
    @JvmStatic
    fun parse(type: Int?, content: String?): Lyric {
        return when (type) {
            Constant.KSC -> KSCLyricParser.parse(content)
            else -> LRCLyricParser.parse(content)
        }
    }
}
