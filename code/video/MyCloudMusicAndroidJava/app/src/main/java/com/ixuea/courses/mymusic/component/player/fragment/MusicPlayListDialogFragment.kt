package com.ixuea.courses.mymusic.component.player.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ixuea.courses.mymusic.component.player.ui.MusicPlayListSheet
import com.ixuea.courses.mymusic.component.song.model.Song
import com.ixuea.courses.mymusic.manager.MusicListManager
import com.ixuea.courses.mymusic.playback.PlaybackService
import com.ixuea.courses.mymusic.ui.compose.MuseFlowTheme

/**
 * 播放列表对话框
 */
class MusicPlayListDialogFragment : BottomSheetDialogFragment() {
    private var songs by mutableStateOf<List<Song>>(emptyList())
    private var currentSongId by mutableStateOf<String?>(null)
    private var loopModel by mutableStateOf(MusicListManager.MODEL_LOOP_LIST)

    private fun removeItem(position: Int) {
        val songs = musicListManager.datum
        if (position !in songs.indices) {
            refreshState()
            return
        }

        musicListManager.delete(position)
        refreshState()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        refreshState()
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MuseFlowTheme {
                    MusicPlayListSheet(
                        songs = songs,
                        currentSongId = currentSongId,
                        loopModel = loopModel,
                        onLoopClick = {
                            musicListManager.changeLoopModel()
                            refreshState()
                        },
                        onDeleteAllClick = {
                            dismiss()
                            musicListManager.deleteAll()
                        },
                        onSongClick = { song ->
                            dismiss()
                            musicListManager.play(song)
                        },
                        onDeleteSongClick = ::removeItem,
                    )
                }
            }
        }
    }

    private fun refreshState() {
        songs = musicListManager.datum.toList()
        currentSongId = musicListManager.data?.id
        loopModel = musicListManager.loopModel
    }

    private val musicListManager: MusicListManager
        get() = PlaybackService.getListManager(requireActivity().applicationContext)

    companion object {
        private const val TAG = "MusicPlayListDialogFragment"

        @JvmStatic
        fun newInstance(): MusicPlayListDialogFragment {
            return MusicPlayListDialogFragment().apply {
                arguments = Bundle()
            }
        }

        /**
         * 显示
         */
        @JvmStatic
        fun show(fragmentManager: FragmentManager) {
            newInstance().show(fragmentManager, TAG)
        }
    }
}
