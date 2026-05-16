package com.ixuea.courses.mymusic.playback

import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.Player
import com.ixuea.courses.mymusic.component.song.model.Song
import com.ixuea.courses.mymusic.manager.MusicListManager
import timber.log.Timber

class LegacyMusicSessionPlayer(
    player: Player,
    private val musicListManager: MusicListManager
) : ForwardingPlayer(player) {
    override fun play() {
        if (musicListManager.data == null && musicListManager.datum.isEmpty()) {
            return
        }

        try {
            musicListManager.resume()
        } catch (exception: Exception) {
            Timber.w(exception, "Unable to handle media session play command")
        }
    }

    override fun pause() {
        try {
            musicListManager.pause()
        } catch (exception: Exception) {
            Timber.w(exception, "Unable to handle media session pause command")
        }
    }

    override fun setPlayWhenReady(playWhenReady: Boolean) {
        if (playWhenReady) {
            play()
        } else {
            pause()
        }
    }

    override fun seekTo(positionMs: Long) {
        musicListManager.seekTo(positionMs.toLegacyProgress())
    }

    override fun seekTo(mediaItemIndex: Int, positionMs: Long) {
        seekTo(positionMs)
    }

    override fun stop() {
        pause()
    }

    override fun hasNext(): Boolean {
        return hasNextMediaItem()
    }

    override fun hasNextWindow(): Boolean {
        return hasNextMediaItem()
    }

    override fun hasNextMediaItem(): Boolean {
        return musicListManager.datum.isNotEmpty()
    }

    override fun next() {
        playNext()
    }

    override fun seekToNextWindow() {
        playNext()
    }

    override fun seekToNextMediaItem() {
        playNext()
    }

    override fun seekToNext() {
        playNext()
    }

    override fun hasPrevious(): Boolean {
        return hasPreviousMediaItem()
    }

    override fun hasPreviousWindow(): Boolean {
        return hasPreviousMediaItem()
    }

    override fun hasPreviousMediaItem(): Boolean {
        return musicListManager.datum.isNotEmpty()
    }

    override fun previous() {
        playPrevious()
    }

    override fun seekToPreviousWindow() {
        playPrevious()
    }

    override fun seekToPreviousMediaItem() {
        playPrevious()
    }

    override fun seekToPrevious() {
        playPrevious()
    }

    private fun playNext() {
        playSongSafely("next") {
            musicListManager.next()
        }
    }

    private fun playPrevious() {
        playSongSafely("previous") {
            musicListManager.previous()
        }
    }

    private fun playSongSafely(action: String, block: () -> Song?) {
        try {
            block()?.let { musicListManager.play(it) }
        } catch (exception: Exception) {
            Timber.w(exception, "Unable to handle media session %s command", action)
        }
    }

    private fun Long.toLegacyProgress(): Int {
        return coerceIn(0L, Int.MAX_VALUE.toLong()).toInt()
    }
}
