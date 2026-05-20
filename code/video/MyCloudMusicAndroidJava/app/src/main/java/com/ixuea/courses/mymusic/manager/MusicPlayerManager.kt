package com.ixuea.courses.mymusic.manager

import com.ixuea.courses.mymusic.component.song.model.Song

/**
 * 音乐播放器对外暴露的接口
 */
interface MusicPlayerManager {
    fun play(uri: String?, data: Song?)

    val isPlaying: Boolean

    fun pause()

    fun resume()

    fun addMusicPlayerListener(listener: MusicPlayerListener?)

    fun removeMusicPlayerListener(listener: MusicPlayerListener?)

    fun seekTo(progress: Int)

    fun setLooping(looping: Boolean)

    fun prepareLyric(data: Song?)
}
