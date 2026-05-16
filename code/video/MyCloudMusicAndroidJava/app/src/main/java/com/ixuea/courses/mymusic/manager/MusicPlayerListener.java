package com.ixuea.courses.mymusic.manager;

import android.media.MediaPlayer;

import com.ixuea.courses.mymusic.component.song.model.Song;

/**
 * 播放器接口
 */
public interface MusicPlayerListener {
    /**
     * 已经暂停了
     */
    default void onPaused(Song data) {

    }

    /**
     * 已经播放了
     */
    default void onPlaying(Song data) {

    }

    /**
     * 播放器准备完毕了
     *
     * @param mp
     * @param data
     */
    default void onPrepared(MediaPlayer mp, Song data) {

    }

    /**
     * 播放进度回调
     *
     * @param data
     */
    default void onProgress(Song data) {

    }

    /**
     * 播放完毕了回调
     *
     * @param mp
     */
    default void onCompletion(MediaPlayer mp) {

    }

    /**
     * 歌词数据改变了
     *
     * @param data
     */
    default void onLyricReady(Song data) {

    }


    /**
     * 播放失败了
     */
    default void onError(Exception exception, Song data) {

    }
}
