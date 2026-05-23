package com.ixuea.courses.mymusic.component.player.fragment

import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ixuea.courses.mymusic.component.player.domain.ObserveMusicPlayListChangesUseCase
import com.ixuea.courses.mymusic.component.player.ui.SmallAudioControlScreen
import com.ixuea.courses.mymusic.component.song.model.Song
import com.ixuea.courses.mymusic.fragment.BaseLogicFragment
import com.ixuea.courses.mymusic.manager.MusicPlayerListener
import com.ixuea.courses.mymusic.manager.MusicPlayerManager
import com.ixuea.courses.mymusic.playback.PlaybackService
import com.ixuea.courses.mymusic.ui.compose.MuseFlowTheme
import kotlinx.coroutines.launch

/**
 * 小的音频播放控制fragment
 * 就是在主界面底部显示的那个小控制条，可以左右滚动切换音乐
 */
class SmallAudioControlPageFragment :
    BaseLogicFragment(),
    MusicPlayerListener {

    private lateinit var musicPlayerManager: MusicPlayerManager
    private val observeMusicPlayListChanges = ObserveMusicPlayListChangesUseCase()

    private var songs by mutableStateOf<List<Song>>(emptyList())
    private var currentSongId by mutableStateOf<String?>(null)
    private var isPlaying by mutableStateOf(false)
    private var progress by mutableStateOf(0)
    private var duration by mutableStateOf(0)

    override fun getLayoutView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: android.os.Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MuseFlowTheme {
                    SmallAudioControlScreen(
                        songs = songs,
                        selectedIndex = selectedIndex(),
                        isPlaying = isPlaying,
                        progress = progress,
                        duration = duration,
                        onSongSettled = { position ->
                            val currentSongs = musicListManager.datum
                            if (position in currentSongs.indices) {
                                val song = currentSongs[position]
                                if (song.id != musicListManager.data?.id) {
                                    musicListManager.play(song)
                                }
                            }
                        },
                        onPlayPauseClick = {
                            if (musicPlayerManager.isPlaying) {
                                musicListManager.pause()
                            } else {
                                musicListManager.resume()
                            }
                        },
                        onListClick = {
                            MusicPlayListDialogFragment.show(childFragmentManager)
                        },
                        onOpenPlayerClick = {
                            startMusicPlayerActivity()
                        },
                    )
                }
            }
        }
    }

    override fun initDatum() {
        super.initDatum()
        musicPlayerManager = PlaybackService.getMusicPlayerManager(
            hostActivity.applicationContext
        )

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

    override fun onResume() {
        super.onResume()
        showMusicInfo()

        musicPlayerManager.addMusicPlayerListener(this)
    }

    /**
     * 界面隐藏了
     */
    override fun onPause() {
        super.onPause()
        musicPlayerManager.removeMusicPlayerListener(this)
    }

    fun showMusicInfo() {
        songs = musicListManager.datum.toList()

        if (songs.isNotEmpty()) {
            val data = musicListManager.data ?: songs[0]
            currentSongId = data.id
            showDuration(data)
            showProgress(data)
            showMusicPlayStatus()
        } else {
            currentSongId = null
            progress = 0
            duration = 0
            isPlaying = false
        }
    }

    /**
     * 显示时长
     */
    private fun showDuration(data: Song) {
        duration = data.duration.toInt()
    }

    /**
     * 显示播放进度
     */
    private fun showProgress(data: Song) {
        progress = data.progress.toInt()
        currentSongId = data.id
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
        isPlaying = false
    }

    /**
     * 显示暂停状态
     */
    private fun showPauseStatus() {
        isPlaying = true
    }

    override fun onPaused(data: Song) {
        showPlayStatus()
    }

    override fun onPlaying(data: Song) {
        showPauseStatus()
    }

    override fun onPrepared(mp: MediaPlayer?, data: Song) {
        showDuration(data)
        currentSongId = data.id
    }

    override fun onProgress(data: Song) {
        showProgress(data)
    }

    private fun selectedIndex(): Int {
        if (songs.isEmpty()) {
            return 0
        }

        val index = songs.indexOfFirst { it.id == currentSongId }
        return if (index == -1) 0 else index
    }
}
