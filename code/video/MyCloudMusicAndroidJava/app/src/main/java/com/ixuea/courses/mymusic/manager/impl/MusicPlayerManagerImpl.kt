package com.ixuea.courses.mymusic.manager.impl

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import com.ixuea.courses.mymusic.component.lyric.parser.LyricParser
import com.ixuea.courses.mymusic.component.song.domain.LoadSongDetailUseCase
import com.ixuea.courses.mymusic.component.song.model.Song
import com.ixuea.courses.mymusic.manager.MusicPlayerListener
import com.ixuea.courses.mymusic.manager.MusicPlayerManager
import com.ixuea.courses.mymusic.manager.SuperAudioManager
import com.ixuea.courses.mymusic.playback.PlaybackController
import com.ixuea.courses.mymusic.playback.PlaybackRepository
import com.ixuea.courses.mymusic.playback.PlaybackService
import java.util.concurrent.CopyOnWriteArrayList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.apache.commons.lang3.StringUtils
import timber.log.Timber

/**
 * 播放管理器默认实现。
 */
class MusicPlayerManagerImpl private constructor(context: Context) : MusicPlayerManager,
    AudioManager.OnAudioFocusChangeListener {
    private val context: Context = context.applicationContext
    private val playbackRepository: PlaybackRepository = PlaybackRepository.getInstance(this.context)
    private val superAudioManager: SuperAudioManager = SuperAudioManager.getInstance(this.context)
    private val loadSongDetail = LoadSongDetailUseCase()
    private val managerScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var lyricLoadJob: Job? = null
    private var uri: String? = null
    private var data: Song? = null
    private val focusLock = Any()

    /**
     * 播放器监听器。
     */
    private val listeners = CopyOnWriteArrayList<MusicPlayerListener>()

    /**
     * 音频焦点获取到了，继续播放。
     */
    private var resumeOnFocusGain = false

    /**
     * 是否准备播放了，也就是是否调用了 prepare 方法。
     */
    private var isPrepare = false

    init {
        playbackRepository.addListener(
            object : PlaybackController.Listener {
                override fun onPrepared(song: Song, durationMs: Long) {
                    song.duration = durationMs
                    listeners.forEach { it.onPrepared(null, song) }
                }

                override fun onPlaying(song: Song) {
                    listeners.forEach { it.onPlaying(song) }
                }

                override fun onPaused(song: Song) {
                    listeners.forEach { it.onPaused(song) }
                }

                override fun onProgress(song: Song, positionMs: Long, durationMs: Long) {
                    song.progress = positionMs
                    if (durationMs > 0) {
                        song.duration = durationMs
                    }

                    listeners.forEach { it.onProgress(song) }
                }

                override fun onCompletion(song: Song) {
                    isPrepare = false
                    listeners.forEach { it.onCompletion(null) }
                }

                override fun onError(exception: Exception, song: Song?) {
                    listeners.forEach { it.onError(exception, song) }
                }
            }
        )
    }

    override fun play(uri: String?, data: Song?) {
        this.uri = uri
        this.data = data

        if (uri.isNullOrBlank() || data == null) {
            return
        }

        if (!requestAudioFocus()) {
            return
        }

        playNow()
    }

    private fun playNow() {
        val currentUri = uri ?: return
        val currentData = data ?: return

        isPrepare = true
        PlaybackService.start(context)
        playbackRepository.play(currentUri, currentData)
        prepareLyric(currentData)
    }

    override fun prepareLyric(data: Song?) {
        this.data = data
        val currentData = data ?: return
        lyricLoadJob?.cancel()
        lyricLoadJob = null

        when {
            currentData.parsedLyric != null -> onLyricReady()
            StringUtils.isNotBlank(currentData.lyric) -> {
                parseLyric()
                onLyricReady()
            }
            currentData.isLocal -> onLyricReady()
            currentData.id.isNullOrBlank() -> onLyricReady()
            else -> {
                loadRemoteLyric(currentData)
            }
        }
    }

    private fun loadRemoteLyric(currentData: Song) {
        val songId = currentData.id.orEmpty()
        lyricLoadJob = managerScope.launch {
            when (val result = loadSongDetail(songId)) {
                is LoadSongDetailUseCase.Result.Success -> {
                    if (data !== currentData) {
                        return@launch
                    }

                    currentData.style = result.song.style
                    currentData.lyric = result.song.lyric

                    if (StringUtils.isNotBlank(currentData.lyric)) {
                        parseLyric()
                    }
                    onLyricReady()
                }

                is LoadSongDetailUseCase.Result.Error -> {
                    if (data === currentData) {
                        onLyricReady()
                    }
                }
            }
        }
    }

    private fun parseLyric() {
        val currentData = data ?: return
        currentData.parsedLyric = LyricParser.parse(currentData.style, currentData.lyric)
    }

    private fun onLyricReady() {
        val currentData = data ?: return
        playbackRepository.updateLyric(currentData, true)
        listeners.forEach { it.onLyricReady(currentData) }
    }

    /**
     * 获取音频焦点。
     */
    private fun requestAudioFocus(): Boolean {
        val audioFocusResult = superAudioManager.requestAudioFocus(this, AudioAttributes.CONTENT_TYPE_MUSIC)
        synchronized(focusLock) {
            if (audioFocusResult != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                resumeOnFocusGain = true
                return false
            }
        }
        return true
    }

    private fun isEmptyListeners(): Boolean {
        return listeners.isEmpty()
    }

    override val isPlaying: Boolean
        get() = playbackRepository.isPlaying()

    override fun pause() {
        if (isPlaying) {
            playbackRepository.pause()
        }
    }

    override fun resume() {
        if (!isPlaying) {
            if (!requestAudioFocus()) {
                return
            }

            resumeNow()
        }
    }

    private fun resumeNow() {
        PlaybackService.start(context)
        playbackRepository.resume()
    }

    override fun addMusicPlayerListener(listener: MusicPlayerListener?) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener)
        }

        playbackRepository.setProgressUpdatesEnabled(!isEmptyListeners())
    }

    override fun removeMusicPlayerListener(listener: MusicPlayerListener?) {
        if (listener != null) {
            listeners.remove(listener)
        }
        playbackRepository.setProgressUpdatesEnabled(!isEmptyListeners())
    }

    override fun seekTo(progress: Int) {
        playbackRepository.seekTo(progress)
    }

    override fun setLooping(looping: Boolean) {
        playbackRepository.setLooping(looping)
    }

    /**
     * 音频焦点改变了回调。
     */
    override fun onAudioFocusChange(focusChange: Int) {
        Timber.d("onAudioFocusChange %s", focusChange)

        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                if (resumeOnFocusGain) {
                    if (isPrepare) {
                        resumeNow()
                    } else {
                        playNow()
                    }

                    resumeOnFocusGain = false
                }
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                if (isPlaying) {
                    pause()
                }
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT,
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                if (isPlaying) {
                    resumeOnFocusGain = true
                    pause()
                }
            }
        }
    }

    companion object {
        @Volatile
        private var instance: MusicPlayerManagerImpl? = null

        @JvmStatic
        @Synchronized
        fun getInstance(context: Context): MusicPlayerManager {
            if (instance == null) {
                instance = MusicPlayerManagerImpl(context)
            }
            return instance!!
        }
    }
}
