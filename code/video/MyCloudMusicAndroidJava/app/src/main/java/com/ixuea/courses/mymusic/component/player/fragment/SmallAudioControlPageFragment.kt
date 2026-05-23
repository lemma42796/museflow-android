package com.ixuea.courses.mymusic.component.player.fragment

import android.media.MediaPlayer
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager.widget.ViewPager
import com.ixuea.courses.mymusic.AppContext
import com.ixuea.courses.mymusic.component.player.adapter.SmallAudioControlAdapter
import com.ixuea.courses.mymusic.component.player.domain.ObserveMusicPlayListChangesUseCase
import com.ixuea.courses.mymusic.component.song.model.Song
import com.ixuea.courses.mymusic.databinding.FragmentSmallAudioControlPageBinding
import com.ixuea.courses.mymusic.fragment.BaseViewModelFragment
import com.ixuea.courses.mymusic.manager.MusicPlayerListener
import com.ixuea.courses.mymusic.manager.MusicPlayerManager
import com.ixuea.courses.mymusic.playback.PlaybackService
import kotlinx.coroutines.launch

/**
 * 小的音频播放控制fragment
 * 就是在主界面底部显示的那个小控制条，可以左右滚动切换音乐
 */
class SmallAudioControlPageFragment :
    BaseViewModelFragment<FragmentSmallAudioControlPageBinding>(),
    MusicPlayerListener {

    private lateinit var adapter: SmallAudioControlAdapter
    private lateinit var musicPlayerManager: MusicPlayerManager
    private val observeMusicPlayListChanges = ObserveMusicPlayListChangesUseCase()

    /**
     * 歌曲滚动监听器
     */
    private val onPageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }

        /**
         * 页面滚动完成了
         * 用这个方法，会导致第一次进入应用就开始播放了
         * 因为要调用scrollPosition方法滚动到当前音乐，用onPageScrollStateChanged不会有这问题
         */
        override fun onPageSelected(position: Int) {
            val songs = musicListManager.datum
            if (position !in songs.indices) {
                return
            }

            musicListManager.play(songs[position])
        }

        /**
         * 滚动状态改变了，例如：现在是静止的，开始滚动了；或者现在是滚动，停止滚动了
         */
        override fun onPageScrollStateChanged(state: Int) {
            if (ViewPager.SCROLL_STATE_IDLE != state) {
                return
            }

            val songs = musicListManager.datum
            val currentItem = binding.list.currentItem
            if (currentItem !in songs.indices) {
                return
            }

            val song = songs[currentItem]
            val currentSong = musicListManager.data
            if (currentSong == null || currentSong.id != song.id) {
                musicListManager.play(song)
            }
        }
    }

    override fun initDatum() {
        super.initDatum()
        musicPlayerManager = PlaybackService.getMusicPlayerManager(
            AppContext.getInstance().applicationContext
        )

        adapter = SmallAudioControlAdapter(hostActivity, childFragmentManager)
        binding.list.adapter = adapter
        observePlayerEvents()
    }

    private fun observePlayerEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                observeMusicPlayListChanges().collect {
                    showMusicInfo()
                }
            }
        }
    }

    override fun initListeners() {
        super.initListeners()
        binding.play.setOnClickListener {
            if (musicPlayerManager.isPlaying) {
                musicListManager.pause()
            } else {
                musicListManager.resume()
            }
        }

        binding.listButton.setOnClickListener {
            MusicPlayListDialogFragment.show(childFragmentManager)
        }
    }

    override fun onResume() {
        super.onResume()
        showMusicInfo()

        musicPlayerManager.addMusicPlayerListener(this)
        binding.list.addOnPageChangeListener(onPageChangeListener)
    }

    /**
     * 界面隐藏了
     */
    override fun onPause() {
        super.onPause()
        musicPlayerManager.removeMusicPlayerListener(this)
        binding.list.removeOnPageChangeListener(onPageChangeListener)
    }

    fun showMusicInfo() {
        val songs = musicListManager.datum
        adapter.setDatum(songs)

        if (songs.isNotEmpty()) {
            binding.container.visibility = View.VISIBLE

            val data = musicListManager.data ?: songs[0]

            scrollPosition(data)
            showDuration(data)
            showProgress(data)
            showMusicPlayStatus()
        } else {
            binding.container.visibility = View.GONE
        }
    }

    /**
     * 显示时长
     */
    private fun showDuration(data: Song) {
        binding.progress.max = data.duration.toInt()
    }

    /**
     * 显示播放进度
     */
    private fun showProgress(data: Song) {
        binding.progress.progress = data.progress.toInt()
    }

    /**
     * 显示播放状态
     */
    private fun showMusicPlayStatus() {
        if (musicPlayerManager.isPlaying) {
            showPauseStatus()
        } else {
            showPlayStatus()
        }
    }

    /**
     * 显示播放状态
     */
    private fun showPlayStatus() {
        // 这种图片切换可以使用Selector来实现
        binding.play.isSelected = false
    }

    /**
     * 显示暂停状态
     */
    private fun showPauseStatus() {
        binding.play.isSelected = true
    }

    /**
     * 滚动到当前音乐位置
     */
    private fun scrollPosition(data: Song) {
        val songs = musicListManager.datum
        val index = songs.indexOf(data)
        if (index != -1) {
            binding.list.setCurrentItem(index, false)
        }
    }

    override fun onPaused(data: Song) {
        showPlayStatus()
    }

    override fun onPlaying(data: Song) {
        showPauseStatus()
    }

    override fun onPrepared(mp: MediaPlayer?, data: Song) {
        showDuration(data)
        scrollPosition(data)
    }

    override fun onProgress(data: Song) {
        binding.progress.progress = data.progress.toInt()
    }

}
