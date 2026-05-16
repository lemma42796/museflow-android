package com.ixuea.courses.mymusic.component.lyric.parser;

import static com.ixuea.courses.mymusic.util.Constant.KSC;

import com.ixuea.courses.mymusic.component.lyric.model.Lyric;

/**
 * 歌词解析器
 */
public class LyricParser {
    /**
     * 解析歌词
     *
     * @param type    歌词类型
     * @param content 歌词内容
     * @return 解析后的歌词对象
     */
    public static Lyric parse(int type, String content) {
        switch (type) {
            case KSC:
                return KSCLyricParser.parse(content);
            default:
                //默认解析LRC歌词
                return LRCLyricParser.parse(content);
        }
    }
}
