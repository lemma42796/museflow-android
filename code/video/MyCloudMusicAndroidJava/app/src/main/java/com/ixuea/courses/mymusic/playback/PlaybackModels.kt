package com.ixuea.courses.mymusic.playback

import com.ixuea.courses.mymusic.component.lyric.model.Lyric
import com.ixuea.courses.mymusic.component.song.model.Song

data class PlaybackState(
    val song: Song? = null,
    val uri: String? = null,
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val durationMs: Long = 0L,
    val progressMs: Long = 0L,
    val errorMessage: String? = null
)

data class QueueState(
    val songs: List<Song> = emptyList(),
    val currentIndex: Int = -1,
    val loopModel: Int = 0
)

data class LyricState(
    val song: Song? = null,
    val lyric: Lyric? = null,
    val isReady: Boolean = false
)
