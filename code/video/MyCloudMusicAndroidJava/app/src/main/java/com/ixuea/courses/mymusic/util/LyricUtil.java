package com.ixuea.courses.mymusic.util;

import com.ixuea.courses.mymusic.component.lyric.model.Line;
import com.ixuea.courses.mymusic.component.lyric.model.Lyric;

import java.util.List;

/**
 * 歌词相关
 */
public class LyricUtil {
    /**
     * 计算当前播放时间是那一行歌词
     *
     * @param data     歌词对象
     * @param position 播放时间
     * @return
     */
    public static int getLineNumber(Lyric data, int position) {
        //先获取歌词列表
        List<Line> datum = data.getDatum();

        //倒序遍历每一行歌词
        Line line = null;
        for (int i = datum.size() - 1; i >= 0; i--) {
            line = datum.get(i);

            //如果当前时间正好大于等于该行开始时间
            //就是该行
            if (position >= line.getStartTime()) {
                return i;
            }
        }

        //默认第0行
        return 0;
    }

    /**
     * 获取当前播放时间对应该行第几个字
     *
     * @param data
     * @param progress
     * @return
     */
    public static int getWordIndex(Line data, long progress) {
        //这一行的开始时间
        long startTime = data.getStartTime();

        //循环所有字
        for (int i = 0; i < data.getWords().length; i++) {
            //累加时间
            startTime += data.getWordDurations()[i];

            if (progress < startTime) {
                //如果进度小于累加的时间
                //就是这个索引
                return i;
            }
        }

        //默认值
        return -1;
    }

    /**
     * 获取当前字播放的时间
     *
     * @param data
     * @param progress
     * @return
     */
    public static float getWordPlayedTime(Line data, long progress) {
        //这一行歌词开始的时间
        long startTime = data.getStartTime();

        //循环所有字
        for (int i = 0; i < data.getWords().length; i++) {
            startTime += data.getWordDurations()[i];

            if (progress < startTime) {
                //计算当前字已经播放的时间
                return data.getWordDurations()[i] - (startTime - progress);
            }
        }

        //默认值
        return -1;
    }

    /**
     * 获取当前时间对应的歌词行
     *
     * @param data
     * @param progress
     * @return
     */
    public static Line getLyricLine(Lyric data, long progress) {
        //获取当前时间的行
        int lineNumber = LyricUtil.getLineNumber(data, (int) progress);

        //获取当前时间歌词行
        return data.getDatum().get(lineNumber);
    }
}
