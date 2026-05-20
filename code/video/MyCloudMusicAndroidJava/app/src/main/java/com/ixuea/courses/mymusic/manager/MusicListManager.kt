package com.ixuea.courses.mymusic.manager

import com.ixuea.courses.mymusic.component.song.model.Song

/**
 * 列表管理器，封装播放列表相关操作，例如上一曲、下一曲、循环模式。
 */
interface MusicListManager {
    var datum: List<Song>

    fun play(data: Song?)

    fun pause()

    fun resume()

    val data: Song?

    fun changeLoopModel(): Int

    val loopModel: Int

    fun previous(): Song?

    fun next(): Song?

    fun delete(position: Int)

    fun deleteAll()

    fun seekTo(progress: Int)

    companion object {
        const val MODEL_LOOP_LIST = 0
        const val MODEL_LOOP_ONE = 1
        const val MODEL_LOOP_RANDOM = 2
    }
}
