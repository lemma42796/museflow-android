package com.ixuea.courses.mymusic.component.player.activity

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.activity.BaseLogicActivity
import com.ixuea.courses.mymusic.component.lyric.model.Lyric
import com.ixuea.courses.mymusic.component.lyric.view.LyricListView
import com.ixuea.courses.mymusic.component.player.ui.SimplePlayerScreen
import com.ixuea.courses.mymusic.component.song.model.Song
import com.ixuea.courses.mymusic.manager.MusicPlayerListener
import com.ixuea.courses.mymusic.manager.MusicPlayerManager
import com.ixuea.courses.mymusic.playback.PlaybackService
import com.ixuea.courses.mymusic.ui.compose.MuseFlowTheme

/**
 * 简单的播放器实现
 * 主要测试音乐播放相关逻辑
 * 因为黑胶唱片界面的逻辑比较复杂
 * 如果在和播放相关逻辑混一起，不好实现
 * 所以我们可以先使用一个简单的播放器
 * 从而把播放器相关逻辑实现完成
 * 然后在对接的黑胶唱片，就相对来说简单一点
 */
class SimplePlayerActivity :
    BaseLogicActivity(),
    MusicPlayerListener {

    private lateinit var musicPlayerManager: MusicPlayerManager
    private var isSeekTracking = false
    private var lyricListView: LyricListView? = null

    private var songs by mutableStateOf<List<Song>>(emptyList())
    private var selectedIndex by mutableStateOf(-1)
    private var titleText by mutableStateOf("")
    private var isPlaying by mutableStateOf(false)
    private var playbackProgress by mutableStateOf(0)
    private var duration by mutableStateOf(0)
    private var loopModel by mutableStateOf(0)
    private var lyricData by mutableStateOf<Lyric?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MuseFlowTheme {
                SimplePlayerScreen(
                    title = titleText.ifBlank {
                        getString(R.string.activity_simple_player)
                    },
                    songs = songs,
                    selectedIndex = selectedIndex,
                    isPlaying = isPlaying,
                    progress = playbackProgress,
                    duration = duration,
                    loopModel = loopModel,
                    lyric = lyricData,
                    onBack = { onBackPressedDispatcher.onBackPressed() },
                    onSongClick = { song ->
                        musicListManager.play(song)
                    },
                    onPreviousClick = ::playPrevious,
                    onPlayPauseClick = ::playOrPause,
                    onNextClick = ::playNext,
                    onLoopClick = {
                        musicListManager.changeLoopModel()
                        showLoopModel()
                    },
                    onSeekChange = { value ->
                        isSeekTracking = true
                        playbackProgress = value
                        musicListManager.seekTo(value)
                    },
                    onSeekFinished = {
                        isSeekTracking = false
                    },
                    onLyricViewReady = { view ->
                        lyricListView = view
                    },
                )
            }
        }
    }

    override fun initDatum() {
        super.initDatum()
        musicPlayerManager = PlaybackService.getMusicPlayerManager(applicationContext)

        refreshPlaylist()
    }

    private fun refreshPlaylist() {
        songs = musicListManager.datum.toList()
        scrollPosition()
    }

    /**
     * 选中当前音乐
     */
    private fun scrollPosition() {
        val data = musicListManager.data
        val index = songs.indexOf(data)
        selectedIndex = index
    }

    private fun playPrevious() {
        val data = resolveAdjacentSong(isPrevious = true) ?: return
        musicListManager.play(data)
    }

    private fun playNext() {
        val data = resolveAdjacentSong(isPrevious = false) ?: return
        musicListManager.play(data)
    }

    private fun resolveAdjacentSong(isPrevious: Boolean): Song? {
        val songs = musicListManager.datum
        if (songs.isEmpty()) {
            return null
        }

        val current = musicListManager.data
        if (current == null || current !in songs) {
            return songs.first()
        }

        return runCatching {
            if (isPrevious) {
                musicListManager.previous()
            } else {
                musicListManager.next()
            }
        }.getOrNull()
    }

    private fun showLoopModel() {
        loopModel = musicListManager.loopModel
    }

    /**
     * 播放或暂停
     */
    private fun playOrPause() {
        if (musicPlayerManager.isPlaying) {
            musicListManager.pause()
        } else {
            musicListManager.resume()
        }
    }

    /**
     * 显示时长
     */
    private fun showDuration() {
        val end = musicListManager.data?.duration?.toInt() ?: 0

        duration = end
    }

    private fun showLyricData() {
        lyricData = musicListManager.data?.parsedLyric
        lyricListView?.setData(lyricData)
    }

    override fun onPrepared(mp: MediaPlayer?, data: Song) {
        showInitData()
        showDuration()
        scrollPosition()
    }

    override fun onProgress(data: Song) {
        if (isSeekTracking) {
            return
        }

        showProgress()
    }

    /**
     * 显示播放进度
     */
    private fun showProgress() {
        playbackProgress = musicListManager.data?.progress?.toInt() ?: 0

        lyricListView?.setProgress(playbackProgress)
    }

    /**
     * 显示初始化数据
     */
    private fun showInitData() {
        musicListManager.data?.let { data ->
            titleText = data.title.orEmpty()
        }
    }

    /**
     * 显示音乐播放状态
     */
    private fun showMusicPlayStatus() {
        isPlaying = musicPlayerManager.isPlaying
    }

    /**
     * 界面可见了
     */
    override fun onResume() {
        super.onResume()

        refreshPlaylist()
        showInitData()
        showDuration()
        showProgress()
        showMusicPlayStatus()
        showLoopModel()
        scrollPosition()
        showLyricData()

        musicPlayerManager.addMusicPlayerListener(this)
    }

    /**
     * 界面不可见了
     */
    override fun onPause() {
        super.onPause()
        musicPlayerManager.removeMusicPlayerListener(this)
    }

    override fun onPaused(data: Song) {
        showPlayStatus()
    }

    override fun onPlaying(data: Song) {
        showPauseStatus()
    }

    override fun onLyricReady(data: Song) {
        showLyricData()
    }

    /**
     * 显示播放状态
     */
    private fun showPlayStatus() {
        isPlaying = false
    }

    /**
     * 显示暂停状态
     */
    private fun showPauseStatus() {
        isPlaying = true
    }
}
