package com.ixuea.courses.mymusic.manager;

import com.ixuea.courses.mymusic.component.song.model.Song;

import java.util.List;

/**
 * 列表管理器
 * 主要是封装了列表相关的操作
 * 例如：上一曲，下一曲，循环模式
 */
public interface MusicListManager {
    /**
     * 列表循环
     */
    int MODEL_LOOP_LIST = 0;

    /**
     * 单曲循环
     */
    int MODEL_LOOP_ONE = 1;

    /**
     * 随机循环
     */
    public static final int MODEL_LOOP_RANDOM = 2;

    /**
     * 获取播放列表
     *
     * @return
     */
    List<Song> getDatum();

    /**
     * 设置播放列表
     *
     * @param datum
     */
    void setDatum(List<Song> datum);

    /**
     * 播放
     *
     * @param data
     */
    void play(Song data);

    /**
     * 暂停
     */
    void pause();

    /**
     * 继续播放
     */
    void resume();

    /**
     * 获取当前播放的音乐
     *
     * @return
     */
    Song getData();

    /**
     * 更改循环模式
     *
     * @return
     */
    int changeLoopModel();

    /**
     * 获取循环模式
     *
     * @return
     */
    int getLoopModel();

    /**
     * 获取上一个
     *
     * @return
     */
    Song previous();

    /**
     * 获取下一个
     *
     * @return
     */
    Song next();

    /**
     * 删除
     *
     * @param position
     */
    void delete(int position);

    /**
     * 删除所有
     */
    void deleteAll();

    /**
     * 跳转到指定位置
     *
     * @param progress
     */
    void seekTo(int progress);
}
