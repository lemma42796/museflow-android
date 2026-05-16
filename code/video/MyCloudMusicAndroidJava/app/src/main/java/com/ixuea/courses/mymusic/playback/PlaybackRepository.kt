package com.ixuea.courses.mymusic.playback

import android.content.Context
import androidx.media3.common.Player
import com.ixuea.courses.mymusic.component.song.model.Song
import kotlinx.coroutines.flow.StateFlow

class PlaybackRepository private constructor(context: Context) {
    private val controller = PlaybackController.getInstance(context.applicationContext)

    val playbackState: StateFlow<PlaybackState> = controller.playbackState
    val queueState: StateFlow<QueueState> = controller.queueState
    val lyricState: StateFlow<LyricState> = controller.lyricState

    val player: Player
        get() = controller.player

    fun addListener(listener: PlaybackController.Listener) {
        controller.addListener(listener)
    }

    fun removeListener(listener: PlaybackController.Listener) {
        controller.removeListener(listener)
    }

    fun setProgressUpdatesEnabled(enabled: Boolean) {
        controller.setProgressUpdatesEnabled(enabled)
    }

    fun setQueue(songs: List<Song>, currentIndex: Int, loopModel: Int) {
        controller.setQueue(songs, currentIndex, loopModel)
    }

    fun updateLyric(song: Song?, lyricReady: Boolean) {
        controller.updateLyric(song, lyricReady)
    }

    fun play(uri: String, song: Song) {
        controller.play(uri, song)
    }

    fun pause() {
        controller.pause()
    }

    fun resume() {
        controller.resume()
    }

    fun seekTo(positionMs: Int) {
        controller.seekTo(positionMs)
    }

    fun setLooping(looping: Boolean) {
        controller.setLooping(looping)
    }

    fun isPlaying(): Boolean {
        return controller.isPlaying()
    }

    companion object {
        @Volatile
        private var instance: PlaybackRepository? = null

        @JvmStatic
        fun getInstance(context: Context): PlaybackRepository {
            return instance ?: synchronized(this) {
                instance ?: PlaybackRepository(context.applicationContext).also { instance = it }
            }
        }
    }
}
