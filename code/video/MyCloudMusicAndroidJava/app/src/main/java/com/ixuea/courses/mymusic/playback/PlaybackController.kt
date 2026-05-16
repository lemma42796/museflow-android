package com.ixuea.courses.mymusic.playback

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.ixuea.courses.mymusic.component.song.model.Song
import java.io.File
import java.util.concurrent.CopyOnWriteArrayList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class PlaybackController private constructor(context: Context) {
    interface Listener {
        fun onPrepared(song: Song, durationMs: Long)

        fun onPlaying(song: Song)

        fun onPaused(song: Song)

        fun onProgress(song: Song, positionMs: Long, durationMs: Long)

        fun onCompletion(song: Song)

        fun onError(exception: Exception, song: Song?)
    }

    private val appContext = context.applicationContext
    private val mainHandler = Handler(Looper.getMainLooper())
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val listeners = CopyOnWriteArrayList<Listener>()

    private val exoPlayer: ExoPlayer = ExoPlayer.Builder(appContext)
        .setLooper(Looper.getMainLooper())
        .build()

    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> = _playbackState

    private val _queueState = MutableStateFlow(QueueState())
    val queueState: StateFlow<QueueState> = _queueState

    private val _lyricState = MutableStateFlow(LyricState())
    val lyricState: StateFlow<LyricState> = _lyricState

    val player: Player
        get() = exoPlayer

    private var currentSong: Song? = null
    private var currentUri: String? = null
    private var progressJob: Job? = null
    private var preparedMediaId: String? = null
    private var progressUpdatesEnabled = false

    @Volatile
    private var playingSnapshot = false

    init {
        exoPlayer.addListener(
            object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_BUFFERING -> {
                            updatePlaybackState(isBuffering = true)
                        }
                        Player.STATE_READY -> {
                            updatePlaybackState(isBuffering = false)
                            dispatchPreparedIfNeeded()
                        }
                        Player.STATE_ENDED -> {
                            playingSnapshot = false
                            stopProgressUpdates()
                            updateProgressFromPlayer()
                            currentSong?.let { song ->
                                listeners.forEach { it.onCompletion(song) }
                            }
                        }
                        Player.STATE_IDLE -> {
                            playingSnapshot = false
                            stopProgressUpdates()
                            updatePlaybackState(isPlaying = false, isBuffering = false)
                        }
                    }
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    playingSnapshot = isPlaying
                    updatePlaybackState(isPlaying = isPlaying, isBuffering = false)

                    currentSong?.let { song ->
                        if (isPlaying) {
                            listeners.forEach { it.onPlaying(song) }
                            startProgressUpdates()
                        } else if (exoPlayer.playbackState != Player.STATE_ENDED && preparedMediaId != null) {
                            listeners.forEach { it.onPaused(song) }
                            stopProgressUpdates()
                        }
                    }
                }

                override fun onPlayerError(error: PlaybackException) {
                    playingSnapshot = false
                    stopProgressUpdates()
                    updatePlaybackState(
                        isPlaying = false,
                        isBuffering = false,
                        errorMessage = error.message
                    )
                    listeners.forEach { it.onError(error, currentSong) }
                }
            }
        )
    }

    fun addListener(listener: Listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener)
        }
    }

    fun removeListener(listener: Listener) {
        listeners.remove(listener)
    }

    fun setProgressUpdatesEnabled(enabled: Boolean) {
        progressUpdatesEnabled = enabled
        if (enabled && playingSnapshot) {
            startProgressUpdates()
        } else if (!enabled) {
            stopProgressUpdates()
        }
    }

    fun setQueue(songs: List<Song>, currentIndex: Int, loopModel: Int) {
        _queueState.value = QueueState(songs = songs.toList(), currentIndex = currentIndex, loopModel = loopModel)
    }

    fun updateLyric(song: Song?, lyricReady: Boolean) {
        _lyricState.value = LyricState(song = song, lyric = song?.parsedLyric, isReady = lyricReady)
    }

    fun play(uri: String, song: Song) {
        currentUri = uri
        currentSong = song
        preparedMediaId = null
        playingSnapshot = false

        updatePlaybackState(
            song = song,
            uri = uri,
            isPlaying = false,
            isBuffering = true,
            progressMs = 0L,
            durationMs = song.duration,
            errorMessage = null
        )

        runOnMain {
            exoPlayer.setMediaItem(createMediaItem(uri, song))
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
        }
    }

    fun pause() {
        playingSnapshot = false
        runOnMain {
            if (exoPlayer.isPlaying) {
                exoPlayer.pause()
            }
        }
    }

    fun resume() {
        runOnMain {
            exoPlayer.play()
        }
    }

    fun seekTo(positionMs: Int) {
        val safePosition = positionMs.coerceAtLeast(0).toLong()
        currentSong?.progress = safePosition
        updatePlaybackState(progressMs = safePosition)
        runOnMain {
            exoPlayer.seekTo(safePosition)
        }
    }

    fun setLooping(looping: Boolean) {
        runOnMain {
            exoPlayer.repeatMode = if (looping) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
        }
    }

    fun isPlaying(): Boolean {
        return playingSnapshot
    }

    private fun runOnMain(block: () -> Unit) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            block()
        } else {
            mainHandler.post(block)
        }
    }

    private fun createMediaItem(uri: String, song: Song): MediaItem {
        val mediaMetadata = MediaMetadata.Builder()
            .setTitle(song.title)
            .setArtist(song.singer?.nickname ?: song.singerNickname)
            .build()

        return MediaItem.Builder()
            .setMediaId(song.id ?: uri)
            .setUri(resolveUri(uri))
            .setMediaMetadata(mediaMetadata)
            .build()
    }

    private fun resolveUri(uri: String): Uri {
        return if (
            uri.startsWith("content://") ||
            uri.startsWith("file://") ||
            uri.startsWith("http://") ||
            uri.startsWith("https://")
        ) {
            Uri.parse(uri)
        } else {
            Uri.fromFile(File(uri))
        }
    }

    private fun dispatchPreparedIfNeeded() {
        val song = currentSong ?: return
        val mediaId = exoPlayer.currentMediaItem?.mediaId ?: currentUri ?: return
        if (preparedMediaId == mediaId) {
            return
        }

        preparedMediaId = mediaId
        val durationMs = normalizeDuration(exoPlayer.duration)
        song.duration = durationMs
        updatePlaybackState(durationMs = durationMs, progressMs = normalizePosition(exoPlayer.currentPosition))
        listeners.forEach { it.onPrepared(song, durationMs) }
    }

    private fun startProgressUpdates() {
        if (!progressUpdatesEnabled || progressJob?.isActive == true) {
            return
        }

        progressJob = scope.launch {
            while (isActive && playingSnapshot) {
                updateProgressFromPlayer()
                delay(PROGRESS_UPDATE_INTERVAL_MS)
            }
        }
    }

    private fun stopProgressUpdates() {
        progressJob?.cancel()
        progressJob = null
    }

    private fun updateProgressFromPlayer() {
        val song = currentSong ?: return
        val positionMs = normalizePosition(exoPlayer.currentPosition)
        val durationMs = normalizeDuration(exoPlayer.duration)
        song.progress = positionMs
        if (durationMs > 0) {
            song.duration = durationMs
        }
        updatePlaybackState(progressMs = positionMs, durationMs = durationMs)
        listeners.forEach { it.onProgress(song, positionMs, durationMs) }
    }

    private fun updatePlaybackState(
        song: Song? = _playbackState.value.song,
        uri: String? = _playbackState.value.uri,
        isPlaying: Boolean = _playbackState.value.isPlaying,
        isBuffering: Boolean = _playbackState.value.isBuffering,
        durationMs: Long = _playbackState.value.durationMs,
        progressMs: Long = _playbackState.value.progressMs,
        errorMessage: String? = _playbackState.value.errorMessage
    ) {
        _playbackState.value = PlaybackState(
            song = song,
            uri = uri,
            isPlaying = isPlaying,
            isBuffering = isBuffering,
            durationMs = durationMs,
            progressMs = progressMs,
            errorMessage = errorMessage
        )
    }

    private fun normalizePosition(positionMs: Long): Long {
        return positionMs.coerceAtLeast(0L)
    }

    private fun normalizeDuration(durationMs: Long): Long {
        return if (durationMs == C.TIME_UNSET || durationMs < 0) 0L else durationMs
    }

    companion object {
        private const val PROGRESS_UPDATE_INTERVAL_MS = 100L

        @Volatile
        private var instance: PlaybackController? = null

        @JvmStatic
        fun getInstance(context: Context): PlaybackController {
            return instance ?: synchronized(this) {
                instance ?: PlaybackController(context.applicationContext).also { instance = it }
            }
        }
    }
}
