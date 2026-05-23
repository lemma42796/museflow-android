package com.ixuea.courses.mymusic.component.lyric.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.compose.setContent
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.activity.BaseLogicActivity
import com.ixuea.courses.mymusic.component.lyric.ui.ShareLyricImageScreen
import com.ixuea.courses.mymusic.component.song.model.Song
import com.ixuea.courses.mymusic.ui.compose.MuseFlowTheme
import com.ixuea.courses.mymusic.util.Constant
import com.ixuea.courses.mymusic.util.ShareUtil
import com.ixuea.courses.mymusic.util.StorageUtil
import com.ixuea.superui.toast.SuperToast
import com.ixuea.superui.util.SuperViewUtil
import timber.log.Timber

/**
 * 分享歌词图片界面
 */
class ShareLyricImageActivity : BaseLogicActivity() {
    private lateinit var data: Song
    private var lyric: String = ""
    private var lyricContentView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        data = extraData()
        lyric = extraId().orEmpty()

        setContent {
            MuseFlowTheme {
                ShareLyricImageScreen(
                    song = data,
                    lyric = lyric,
                    onBack = { onBackPressedDispatcher.onBackPressed() },
                    onShareClick = ::shareClick,
                    onContentViewReady = { view ->
                        lyricContentView = view
                    },
                )
            }
        }
    }

    private fun shareClick() {
        val contentView = lyricContentView
        if (contentView == null) {
            SuperToast.show(R.string.error_share_failed)
            return
        }

        val bitmap = SuperViewUtil.captureBitmap(contentView)
        val uri = StorageUtil.savePicture(hostActivity, bitmap)

        if (uri != null) {
            val path = StorageUtil.getMediaStorePath(hostActivity, uri)
            Timber.d("shareClick %s", path)

            ShareUtil.shareImage(hostActivity, path)
        } else {
            SuperToast.show(R.string.error_share_failed)
        }
    }

    companion object {
        /**
         * 启动方法
         */
        @JvmStatic
        fun start(activity: Activity, data: Song, lyric: String) {
            val intent = Intent(activity, ShareLyricImageActivity::class.java).apply {
                putExtra(Constant.DATA, data)
                putExtra(Constant.ID, lyric)
            }
            activity.startActivity(intent)
        }
    }
}
