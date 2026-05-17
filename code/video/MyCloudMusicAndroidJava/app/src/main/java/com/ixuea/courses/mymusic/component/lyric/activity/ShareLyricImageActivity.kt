package com.ixuea.courses.mymusic.component.lyric.activity

import android.app.Activity
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.activity.BaseTitleActivity
import com.ixuea.courses.mymusic.component.song.model.Song
import com.ixuea.courses.mymusic.databinding.ActivityShareLyricImageBinding
import com.ixuea.courses.mymusic.util.Constant
import com.ixuea.courses.mymusic.util.ImageUtil
import com.ixuea.courses.mymusic.util.ShareUtil
import com.ixuea.courses.mymusic.util.StorageUtil
import com.ixuea.superui.toast.SuperToast
import com.ixuea.superui.util.SuperViewUtil
import timber.log.Timber

/**
 * 分享歌词图片界面
 */
class ShareLyricImageActivity : BaseTitleActivity<ActivityShareLyricImageBinding>() {
    private var data: Song? = null
    private var lyric: String = ""

    override fun initDatum() {
        super.initDatum()
        data = extraData()
        lyric = extraId().orEmpty()

        val song = data ?: return
        ImageUtil.show(hostActivity, binding.icon, song.icon)
        binding.lyric.text = lyric
        binding.song.text = getString(
            R.string.share_song_name,
            song.singer?.nickname.orEmpty(),
            song.title
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_share_lyric_image, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_share) {
            shareClick()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun shareClick() {
        val bitmap = SuperViewUtil.captureBitmap(binding.lyricContainer)
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
