package com.ixuea.courses.mymusic.component.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.widget.RemoteViews
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.service.MusicPlayerService
import com.ixuea.courses.mymusic.util.Constant
import com.ixuea.courses.mymusic.util.IntentUtil
import com.ixuea.courses.mymusic.util.ResourceUtil
import com.ixuea.courses.mymusic.util.ServiceUtil

/**
 * 音乐桌面微件
 *
 * https://developer.android.google.cn/guide/topics/appwidgets
 */
class MusicWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        ServiceUtil.startService(context.applicationContext, MusicPlayerService::class.java)
        val musicListManager = MusicPlayerService.getListManager(context.applicationContext)
        val data = musicListManager.data

        appWidgetIds.forEach { appWidgetId ->
            val views = RemoteViews(context.packageName, R.layout.music_widget)
            val iconPendingIntent = IntentUtil.createMainActivityPendingIntent(
                context,
                Constant.ACTION_MUSIC_PLAYER_PAGE,
            )
            val previousPendingIntent = IntentUtil.createMusicPlayerServicePendingIntent(
                context,
                Constant.ACTION_PREVIOUS,
            )
            val playPendingIntent = IntentUtil.createMusicPlayerServicePendingIntent(
                context,
                Constant.ACTION_PLAY,
            )
            val nextPendingIntent = IntentUtil.createMusicPlayerServicePendingIntent(
                context,
                Constant.ACTION_NEXT,
            )
            val lyricPendingIntent = IntentUtil.createMusicPlayerServicePendingIntent(
                context,
                Constant.ACTION_LYRIC,
            )

            views.setOnClickPendingIntent(R.id.icon, iconPendingIntent)
            views.setOnClickPendingIntent(R.id.previous, previousPendingIntent)
            views.setOnClickPendingIntent(R.id.play, playPendingIntent)
            views.setOnClickPendingIntent(R.id.next, nextPendingIntent)
            views.setOnClickPendingIntent(R.id.lyric, lyricPendingIntent)

            if (data == null) {
                appWidgetManager.updateAppWidget(appWidgetId, views)
            } else {
                views.setTextViewText(
                    R.id.title,
                    String.format("%s - %s", data.title.orEmpty(), data.singer?.nickname.orEmpty()),
                )
                views.setProgressBar(
                    R.id.progress,
                    data.duration.toInt(),
                    data.progress.toInt(),
                    false,
                )

                Glide.with(context)
                    .asBitmap()
                    .load(ResourceUtil.resourceUri(data.icon))
                    .apply(RequestOptions().centerCrop())
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?,
                        ) {
                            views.setImageViewBitmap(R.id.icon, resource)
                            appWidgetManager.updateAppWidget(appWidgetId, views)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            views.setImageViewBitmap(
                                R.id.icon,
                                BitmapFactory.decodeResource(context.resources, R.drawable.placeholder),
                            )
                            appWidgetManager.updateAppWidget(appWidgetId, views)
                        }
                    })
            }
        }
    }
}
