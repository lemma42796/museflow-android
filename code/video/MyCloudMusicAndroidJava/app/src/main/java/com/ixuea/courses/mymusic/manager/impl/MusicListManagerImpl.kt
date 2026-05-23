package com.ixuea.courses.mymusic.manager.impl

import android.content.Context
import android.media.MediaPlayer
import com.ixuea.android.downloader.domain.DownloadInfo
import com.ixuea.courses.mymusic.AppContext
import com.ixuea.courses.mymusic.component.player.domain.NotifyMusicPlayListChangedUseCase
import com.ixuea.courses.mymusic.component.song.model.Song
import com.ixuea.courses.mymusic.manager.MusicListManager
import com.ixuea.courses.mymusic.manager.MusicPlayerListener
import com.ixuea.courses.mymusic.manager.MusicPlayerManager
import com.ixuea.courses.mymusic.playback.PlaybackRepository
import com.ixuea.courses.mymusic.playback.PlaybackService
import com.ixuea.courses.mymusic.util.Constant
import com.ixuea.courses.mymusic.util.DataUtil
import com.ixuea.courses.mymusic.util.LiteORMUtil
import com.ixuea.courses.mymusic.util.PreferenceUtil
import com.ixuea.courses.mymusic.util.ResourceUtil
import java.util.LinkedList
import java.util.Random
import org.apache.commons.lang3.StringUtils
import timber.log.Timber

/**
 * 列表管理器默认实现。
 */
class MusicListManagerImpl private constructor(context: Context) : MusicListManager, MusicPlayerListener {
    private val context: Context = context.applicationContext
    private val musicPlayerManager: MusicPlayerManager = PlaybackService.getMusicPlayerManager(this.context)
    private val playbackRepository: PlaybackRepository = PlaybackRepository.getInstance(this.context)
    private val playlist = LinkedList<Song>()
    private var isPlay = false
    private var currentSong: Song? = null
    private var model = MusicListManager.MODEL_LOOP_LIST
    private val sp: PreferenceUtil = PreferenceUtil.getInstance(this.context)
    private var lastTime: Long = 0
    private val notifyMusicPlayListChanged = NotifyMusicPlayListChangedUseCase()

    init {
        musicPlayerManager.addMusicPlayerListener(this)
        initPlayList()
        updatePlaybackQueueState()
    }

    /**
     * 初始化播放列表。
     */
    private fun initPlayList() {
        val queryDatum = orm.queryPlayList()
        if (queryDatum.isEmpty()) {
            return
        }

        playlist.clear()
        playlist.addAll(queryDatum)

        val id = sp.lastPlaySongId
        if (StringUtils.isNotBlank(id)) {
            for (song in queryDatum) {
                if (song.id == id) {
                    currentSong = song
                    break
                }
            }

            if (currentSong == null) {
                defaultPlaySong()
            }
        } else {
            defaultPlaySong()
        }
    }

    /**
     * 设置默认播放音乐。
     */
    private fun defaultPlaySong() {
        currentSong = playlist[0]
    }

    override var datum: List<Song>
        get() = playlist
        set(value) {
            DataUtil.changePlayListFlag(playlist, false)
            saveAll()

            playlist.clear()
            playlist.addAll(value)

            DataUtil.changePlayListFlag(playlist, true)
            saveAll()

            updatePlaybackQueueState()
            sendPlayListChangedEvent(0)
        }

    /**
     * 保存播放列表。
     */
    private fun saveAll() {
        orm.saveAll(playlist)
    }

    private val orm: LiteORMUtil
        get() = LiteORMUtil.getInstance(context)

    override fun play(data: Song?) {
        if (data == null) {
            Timber.w("Ignore play request because song is null")
            return
        }

        data.isRotate = true
        isPlay = true
        currentSong = data
        updatePlaybackQueueState()

        if (StringUtils.isNotBlank(data.path)) {
            musicPlayerManager.play(data.path, data)
        } else {
            val downloadInfo = AppContext.getInstance().downloadManager.getDownloadById(data.id)
            if (downloadInfo != null && downloadInfo.status == DownloadInfo.STATUS_COMPLETED) {
                musicPlayerManager.play(downloadInfo.path, data)
                Timber.d("play offline %s %s %s", data.title, downloadInfo.path, data.uri)
            } else {
                val path = ResourceUtil.resourceUri(data.uri)
                musicPlayerManager.play(path, data)
                Timber.d("play online %s %s", data.title, path)
            }
        }

        sp.lastPlaySongId = data.id
    }

    override fun pause() {
        musicPlayerManager.pause()
    }

    override fun resume() {
        val song = currentSong
        if (song == null) {
            Timber.w("Ignore resume request because current song is null")
            return
        }

        if (isPlay) {
            musicPlayerManager.resume()
        } else {
            play(song)
            if (song.progress > 0) {
                musicPlayerManager.seekTo(song.progress.toInt())
            }
        }
    }

    override val data: Song?
        get() = currentSong

    override fun changeLoopModel(): Int {
        model++
        if (model > MusicListManager.MODEL_LOOP_RANDOM) {
            model = MusicListManager.MODEL_LOOP_LIST
        }

        musicPlayerManager.setLooping(MusicListManager.MODEL_LOOP_ONE == model)

        updatePlaybackQueueState()
        return model
    }

    override val loopModel: Int
        get() = model

    override fun previous(): Song? {
        if (playlist.isEmpty()) {
            return null
        }

        val index = when (model) {
            MusicListManager.MODEL_LOOP_RANDOM -> Random().nextInt(playlist.size)
            else -> {
                val currentIndex = playlist.indexOf(currentSong)
                if (currentIndex != -1) {
                    if (currentIndex == 0) {
                        playlist.size - 1
                    } else {
                        currentIndex - 1
                    }
                } else {
                    throw IllegalArgumentException("Cant't find current song")
                }
            }
        }

        return playlist[index]
    }

    override fun next(): Song? {
        if (playlist.isEmpty()) {
            return null
        }

        val index = when (model) {
            MusicListManager.MODEL_LOOP_RANDOM -> Random().nextInt(playlist.size)
            else -> {
                val currentIndex = playlist.indexOf(currentSong)
                if (currentIndex != -1) {
                    if (currentIndex == playlist.size - 1) {
                        0
                    } else {
                        currentIndex + 1
                    }
                } else {
                    throw IllegalArgumentException("Cant'found current song")
                }
            }
        }

        return playlist[index]
    }

    override fun delete(position: Int) {
        val song = playlist[position]
        val current = currentSong
        if (current != null && song.id == current.id) {
            pause()

            val next = next()
            if (next?.id == current.id) {
                currentSong = null
            } else {
                play(next)
            }
        }

        playlist.remove(song)
        orm.deleteSong(song)

        updatePlaybackQueueState()
        sendPlayListChangedEvent(position)
    }

    private fun sendPlayListChangedEvent(position: Int) {
        notifyMusicPlayListChanged(position)
    }

    /**
     * 播放完毕了回调。
     */
    override fun onCompletion(mp: MediaPlayer?) {
        if (model != MusicListManager.MODEL_LOOP_ONE) {
            val nextSong = next()
            if (nextSong != null) {
                play(nextSong)
            }
        }
    }

    override fun onProgress(data: Song) {
        val currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis - lastTime > Constant.SAVE_PROGRESS_TIME) {
            orm.saveSong(data)
            lastTime = currentTimeMillis
        }
    }

    override fun deleteAll() {
        if (musicPlayerManager.isPlaying) {
            pause()
        }

        playlist.clear()
        orm.deletePlayListSong()

        updatePlaybackQueueState()
        sendPlayListChangedEvent(-1)
    }

    override fun seekTo(progress: Int) {
        if (currentSong == null) {
            Timber.w("Ignore seek request because current song is null")
            return
        }

        if (!musicPlayerManager.isPlaying) {
            resume()
        }

        musicPlayerManager.seekTo(progress)
    }

    private fun updatePlaybackQueueState() {
        val currentIndex = if (currentSong == null) -1 else playlist.indexOf(currentSong)
        playbackRepository.setQueue(playlist, currentIndex, model)
    }

    companion object {
        @Volatile
        private var instance: MusicListManagerImpl? = null

        /**
         * 销毁实例。
         */
        @JvmStatic
        fun destroy() {
            instance = null
        }

        @JvmStatic
        @Synchronized
        fun getInstance(context: Context): MusicListManager {
            if (instance == null) {
                instance = MusicListManagerImpl(context)
            }
            return instance!!
        }
    }
}
