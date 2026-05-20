package com.ixuea.courses.mymusic.component.player.fragment

import android.os.Bundle
import com.ixuea.courses.mymusic.activity.BaseLogicActivity
import com.ixuea.courses.mymusic.component.song.model.Song
import com.ixuea.courses.mymusic.databinding.FragmentAudioControlBinding
import com.ixuea.courses.mymusic.fragment.BaseViewModelFragment
import com.ixuea.courses.mymusic.manager.MusicPlayerListener
import com.ixuea.courses.mymusic.manager.MusicPlayerManager
import com.ixuea.courses.mymusic.playback.PlaybackService
import com.ixuea.courses.mymusic.util.Constant
import com.ixuea.courses.mymusic.util.ImageUtil
import com.ixuea.courses.mymusic.util.LyricUtil

/**
 * 小的音频播放音乐fragment
 */
class SmallAudioControlFragment :
    BaseViewModelFragment<FragmentAudioControlBinding>(),
    MusicPlayerListener {

    private lateinit var musicPlayerManager: MusicPlayerManager

    override fun initViews() {
        super.initViews()
        // 这一行歌词始终是选中状态
        binding.lyricLine.setLineSelected(true)
    }

    override fun initDatum() {
        super.initDatum()
        val data = extraData<Song>()

        ImageUtil.show(hostActivity, binding.icon, data.icon)
        binding.title.text = data.title

        musicPlayerManager = PlaybackService.getMusicPlayerManager(
            hostActivity.applicationContext
        )
    }

    override fun initListeners() {
        super.initListeners()
        binding.container.setOnClickListener {
            (hostActivity as BaseLogicActivity).startMusicPlayerActivity()
        }
    }

    /**
     * 因为外面使用的是ViewPager2控件，所以在这里就能准确的监听当前显示的界面
     */
    override fun onResume() {
        super.onResume()
        musicPlayerManager.addMusicPlayerListener(this)

        musicListManager.data?.let { showLyricData(it) }
    }

    override fun onPause() {
        super.onPause()
        musicPlayerManager.removeMusicPlayerListener(this)
    }

    override fun onProgress(data: Song) {
        showLyricData(data)
    }

    override fun onLyricReady(data: Song) {
        showLyricData(data)
    }

    private fun showLyricData(data: Song) {
        val lyric = data.parsedLyric
        if (lyric == null) {
            binding.lyricLine.setData(null)
            return
        }

        val lines = lyric.datum
        if (lines.isNullOrEmpty()) {
            binding.lyricLine.setData(null)
            return
        }

        val progress = data.progress
        val lineNumber = LyricUtil.getLineNumber(lyric, progress.toInt()).coerceIn(lines.indices)
        val line = lines[lineNumber]

        binding.lyricLine.setData(line)
        binding.lyricLine.setAccurate(lyric.isAccurate)

        if (lyric.isAccurate) {
            val lyricCurrentWordIndex = LyricUtil.getWordIndex(line, progress)
            val wordPlayedTime = LyricUtil.getWordPlayedTime(line, progress)

            binding.lyricLine.setLyricCurrentWordIndex(lyricCurrentWordIndex)
            binding.lyricLine.setWordPlayedTime(wordPlayedTime)
            binding.lyricLine.onProgress()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(data: Song): SmallAudioControlFragment {
            return SmallAudioControlFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(Constant.DATA, data)
                }
            }
        }
    }
}
