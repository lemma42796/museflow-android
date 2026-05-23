package com.ixuea.courses.mymusic.manager

import android.media.MediaPlayer
import com.ixuea.courses.mymusic.component.song.model.Song

/**
 * Legacy playback callback surface used by the XML player, widget, and lyric bridge.
 */
interface MusicPlayerListener {
    /**
     * 已经暂停了。
     */
    fun onPaused(data: Song) {
    }

    /**
     * 已经播放了。
     */
    fun onPlaying(data: Song) {
    }

    /**
     * 播放器准备完毕了。
     */
    fun onPrepared(mp: MediaPlayer?, data: Song) {
    }

    /**
     * 播放进度回调。
     */
    fun onProgress(data: Song) {
    }

    /**
     * 播放完毕了回调。
     */
    fun onCompletion(mp: MediaPlayer?) {
    }

    /**
     * 歌词数据改变了。
     */
    fun onLyricReady(data: Song) {
    }

    /**
     * 播放失败了。
     */
    fun onError(exception: Exception, data: Song?) {
    }
}
