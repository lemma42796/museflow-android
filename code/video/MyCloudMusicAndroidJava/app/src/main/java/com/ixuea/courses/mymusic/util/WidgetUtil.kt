package com.ixuea.courses.mymusic.util

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.graphics.Bitmap
import android.widget.RemoteViews
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.component.song.model.Song
import com.ixuea.courses.mymusic.component.widget.MusicWidget

/**
 * 微件（Widget）工具类。
 */
object WidgetUtil {
    /**
     * 播放时。
     */
    @JvmStatic
    fun onPlaying(context: Context) {
        val views = getMusicWidgetRemoteViews(context)
        views.setImageViewResource(R.id.play, R.drawable.music_pause)
        update(context, views)
    }

    /**
     * 暂停时。
     */
    @JvmStatic
    fun onPaused(context: Context) {
        val views = getMusicWidgetRemoteViews(context)
        views.setImageViewResource(R.id.play, R.drawable.music_play)
        update(context, views)
    }

    /**
     * 准备完成了。
     */
    @JvmStatic
    fun onPrepared(context: Context, data: Song, icon: Bitmap) {
        val views = getMusicWidgetRemoteViews(context)
        views.setTextViewText(R.id.title, String.format("%s - %s", data.title, data.singer?.nickname.orEmpty()))
        views.setProgressBar(R.id.progress, data.duration.toInt(), data.progress.toInt(), false)
        views.setImageViewBitmap(R.id.icon, icon)
        update(context, views)
    }

    /**
     * 播放进度改变时。
     */
    @JvmStatic
    fun onProgress(context: Context, data: Song) {
        val views = getMusicWidgetRemoteViews(context)
        views.setProgressBar(R.id.progress, data.duration.toInt(), data.progress.toInt(), false)
        update(context, views)
    }

    /**
     * 桌面歌词显示状态改变了。
     */
    @JvmStatic
    fun onGlobalLyricShowStatusChanged(context: Context, isShow: Boolean) {
        val contentView = getMusicWidgetRemoteViews(context)
        contentView.setImageViewResource(R.id.lyric, if (isShow) R.drawable.ic_lyric else R.drawable.ic_lyric_selected)
        update(context, contentView)
    }

    private fun update(context: Context, views: RemoteViews) {
        val manager = AppWidgetManager.getInstance(context)
        val componentName = ComponentName(context, MusicWidget::class.java)
        manager.updateAppWidget(componentName, views)
    }

    private fun getMusicWidgetRemoteViews(context: Context): RemoteViews {
        return RemoteViews(context.packageName, R.layout.music_widget)
    }
}
