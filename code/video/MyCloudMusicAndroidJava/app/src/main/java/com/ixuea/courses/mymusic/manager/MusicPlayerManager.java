package com.ixuea.courses.mymusic.manager;

import com.ixuea.courses.mymusic.component.song.model.Song;

/**
 * 音乐播放器对外暴露的接口
 */
public interface MusicPlayerManager {
    /**
     * 播放
     *
     * @param uri  播放音乐的绝对地址
     * @param data 音乐对象
     */
    void play(String uri, Song data);

    /**
     * 是否播放了
     *
     * @return
     */
    boolean isPlaying();

    /**
     * 暂停
     */
    void pause();

    /**
     * 继续播放
     */
    void resume();

    /**
     * 添加播放监听器
     *
     * @param listener
     */
    void addMusicPlayerListener(MusicPlayerListener listener);

    /**
     * 移除播放监听器
     *
     * @param listener
     */
    void removeMusicPlayerListener(MusicPlayerListener listener);

    /**
     * 从指定位置播放
     *
     * @param progress 单位:毫秒
     */
    void seekTo(int progress);

    /**
     * 设置是否单曲循环
     *
     * @param looping
     */
    void setLooping(boolean looping);

    /**
     * 准备歌词
     */
    void prepareLyric(Song data);
}
