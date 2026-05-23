package com.ixuea.courses.mymusic.component.player.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.viewinterop.AndroidView
import com.ixuea.courses.mymusic.component.player.domain.NotifyRecordClickedUseCase
import com.ixuea.courses.mymusic.component.player.view.RecordView
import com.ixuea.courses.mymusic.component.song.model.Song
import com.ixuea.courses.mymusic.fragment.BaseLogicFragment
import com.ixuea.courses.mymusic.manager.MusicPlayerListener
import com.ixuea.courses.mymusic.manager.MusicPlayerManager
import com.ixuea.courses.mymusic.playback.PlaybackService
import com.ixuea.courses.mymusic.ui.compose.MuseFlowTheme
import com.ixuea.courses.mymusic.util.Constant
import com.ixuea.courses.mymusic.util.ImageUtil

/**
 * 音乐黑胶唱片界面
 */
class RecordFragment : BaseLogicFragment(), MusicPlayerListener {
    private lateinit var musicPlayerManager: MusicPlayerManager
    private val notifyRecordClicked = NotifyRecordClickedUseCase()
    private var recordView: RecordView? = null
    private lateinit var data: Song

    override fun getLayoutView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        data = extraData()
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MuseFlowTheme {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { context ->
                            RecordView(context).apply {
                                setOnClickListener {
                                    notifyRecordClicked()
                                }
                            }
                        },
                        update = { view ->
                            recordView = view
                            ImageUtil.show(hostActivity, view.iconView, data.icon)
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
    }

    /**
     * 界面可见了
     */
    override fun onResume() {
        super.onResume()
        musicPlayerManager.addMusicPlayerListener(this)
    }

    /**
     * 界面不可见了
     */
    override fun onPause() {
        super.onPause()
        musicPlayerManager.removeMusicPlayerListener(this)
    }

    override fun onProgress(data: Song) {
        if (!data.isRotate) {
            return
        }

        recordView?.incrementRotate()
    }

    override fun onDestroyView() {
        recordView = null
        super.onDestroyView()
    }

    companion object {
        @JvmStatic
        fun newInstance(data: Song): RecordFragment {
            return RecordFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(Constant.DATA, data)
                }
            }
        }
    }
}
