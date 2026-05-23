package com.ixuea.courses.mymusic.component.player.fragment

import android.os.Bundle
import com.ixuea.courses.mymusic.component.player.domain.NotifyRecordClickedUseCase
import com.ixuea.courses.mymusic.component.song.model.Song
import com.ixuea.courses.mymusic.databinding.FragmentRecordBinding
import com.ixuea.courses.mymusic.fragment.BaseViewModelFragment
import com.ixuea.courses.mymusic.manager.MusicPlayerListener
import com.ixuea.courses.mymusic.manager.MusicPlayerManager
import com.ixuea.courses.mymusic.playback.PlaybackService
import com.ixuea.courses.mymusic.util.Constant
import com.ixuea.courses.mymusic.util.ImageUtil

/**
 * 音乐黑胶唱片界面
 */
class RecordFragment : BaseViewModelFragment<FragmentRecordBinding>(), MusicPlayerListener {
    private lateinit var musicPlayerManager: MusicPlayerManager
    private val notifyRecordClicked = NotifyRecordClickedUseCase()

    override fun initDatum() {
        super.initDatum()
        val data = extraData<Song>()

        ImageUtil.show(hostActivity, binding.record.binding.icon, data.icon)

        musicPlayerManager = PlaybackService.getMusicPlayerManager(
            hostActivity.applicationContext
        )
    }

    override fun initListeners() {
        super.initListeners()
        binding.container.setOnClickListener {
            notifyRecordClicked()
        }
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

        binding.record.incrementRotate()
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
