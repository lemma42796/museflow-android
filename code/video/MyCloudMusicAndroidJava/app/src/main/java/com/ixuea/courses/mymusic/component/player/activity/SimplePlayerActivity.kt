package com.ixuea.courses.mymusic.component.player.activity

import android.media.MediaPlayer
import android.widget.SeekBar
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.activity.BaseTitleActivity
import com.ixuea.courses.mymusic.component.player.adapter.SimplePlayerAdapter
import com.ixuea.courses.mymusic.component.song.model.Song
import com.ixuea.courses.mymusic.databinding.ActivitySimplePlayerBinding
import com.ixuea.courses.mymusic.manager.MusicPlayerListener
import com.ixuea.courses.mymusic.manager.MusicPlayerManager
import com.ixuea.courses.mymusic.playback.PlaybackService
import com.ixuea.courses.mymusic.util.PlayListUtil
import com.ixuea.courses.mymusic.util.SuperDateUtil

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
    BaseTitleActivity<ActivitySimplePlayerBinding>(),
    MusicPlayerListener,
    SeekBar.OnSeekBarChangeListener {

    private lateinit var musicPlayerManager: MusicPlayerManager
    private lateinit var adapter: SimplePlayerAdapter
    private var isSeekTracking = false

    override fun initDatum() {
        super.initDatum()
        musicPlayerManager = PlaybackService.getMusicPlayerManager(applicationContext)

        adapter = SimplePlayerAdapter(android.R.layout.simple_list_item_1)
        binding.list.adapter = adapter

        refreshPlaylist()
    }

    private fun refreshPlaylist() {
        adapter.setNewInstance(musicListManager.datum.toMutableList())
    }

    /**
     * 选中当前音乐
     */
    private fun scrollPosition() {
        binding.list.post {
            val songs = musicListManager.datum
            val data = musicListManager.data
            val index = songs.indexOf(data)

            if (index != -1) {
                binding.list.smoothScrollToPosition(index)
                adapter.setSelectedIndex(index)
            } else {
                adapter.setSelectedIndex(-1)
            }
        }
    }

    override fun initListeners() {
        super.initListeners()
        adapter.setOnItemClickListener { _, _, position ->
            val data = adapter.getItem(position)
            musicListManager.play(data)
        }

        binding.progress.setOnSeekBarChangeListener(this)

        binding.previous.setOnClickListener {
            playPrevious()
        }

        binding.play.setOnClickListener {
            playOrPause()
        }

        binding.next.setOnClickListener {
            playNext()
        }

        binding.loopModel.setOnClickListener {
            musicListManager.changeLoopModel()
            showLoopModel()
        }
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
        PlayListUtil.showLoopModel(musicListManager.loopModel, binding.loopModel)
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

        binding.end.text = SuperDateUtil.ms2ms(end)
        binding.progress.max = end
    }

    private fun showLyricData() {
        binding.lyricList.setData(musicListManager.data?.parsedLyric)
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
        val progress = musicListManager.data?.progress?.toInt() ?: 0

        binding.start.text = SuperDateUtil.ms2ms(progress)
        binding.progress.progress = progress
        binding.lyricList.setProgress(progress)
    }

    /**
     * 显示初始化数据
     */
    private fun showInitData() {
        musicListManager.data?.let { data ->
            title = data.title
        }
    }

    /**
     * 显示音乐播放状态
     */
    private fun showMusicPlayStatus() {
        if (musicPlayerManager.isPlaying) {
            showPauseStatus()
        } else {
            showPlayStatus()
        }
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
        binding.play.setText(R.string.play)
    }

    /**
     * 显示暂停状态
     */
    private fun showPauseStatus() {
        binding.play.setText(R.string.pause)
    }

    /**
     * 进度条改变了
     */
    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        if (fromUser) {
            musicListManager.seekTo(progress)
        }
    }

    /**
     * 开始拖拽进度条
     */
    override fun onStartTrackingTouch(seekBar: SeekBar) {
        isSeekTracking = true
    }

    /**
     * 停止拖拽进度条
     */
    override fun onStopTrackingTouch(seekBar: SeekBar) {
        isSeekTracking = false
    }
}
