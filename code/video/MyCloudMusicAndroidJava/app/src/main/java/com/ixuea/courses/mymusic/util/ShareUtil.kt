package com.ixuea.courses.mymusic.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.component.song.model.Song
import java.io.File

object ShareUtil {
    @JvmStatic
    fun shareLyricText(context: Context, data: Song?, lyric: String) {
        val title = data?.title.orEmpty()
        val shareContent = String.format("分享歌词：%s %s", lyric, title)
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, shareContent)
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.share)))
    }

    @JvmStatic
    fun shareLyricTextToQQ(context: Context, data: Song?, lyric: String) {
        shareLyricText(context, data, lyric)
    }

    @JvmStatic
    fun shareImage(context: Context, path: String?) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(File(path!!)))
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.share)))
    }
}
