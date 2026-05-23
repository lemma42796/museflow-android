package com.ixuea.courses.mymusic.util

import android.content.Context
import android.graphics.Bitmap
import com.ixuea.courses.mymusic.component.song.model.Song
import com.ixuea.courses.mymusic.component.widget.MusicWidgetStore

/**
 * 微件（Widget）工具类。
 */
object WidgetUtil {
    /**
     * 播放时。
     */
    @JvmStatic
    fun onPlaying(context: Context) {
        MusicWidgetStore.onPlaying(context)
    }

    /**
     * 暂停时。
     */
    @JvmStatic
    fun onPaused(context: Context) {
        MusicWidgetStore.onPaused(context)
    }

    /**
     * 准备完成了。
     */
    @JvmStatic
    fun onPrepared(context: Context, data: Song, icon: Bitmap) {
        MusicWidgetStore.onPrepared(context, data, icon)
    }

    /**
     * 播放进度改变时。
     */
    @JvmStatic
    fun onProgress(context: Context, data: Song) {
        MusicWidgetStore.onProgress(context, data)
    }

    /**
     * 桌面歌词显示状态改变了。
     */
    @JvmStatic
    fun onGlobalLyricShowStatusChanged(context: Context, isShow: Boolean) {
        MusicWidgetStore.onGlobalLyricShowStatusChanged(context, isShow)
    }
}
