package com.ixuea.courses.mymusic.util;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.RemoteViews;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.component.song.model.Song;
import com.ixuea.courses.mymusic.component.widget.MusicWidget;

/**
 * 微件（Widget）工具类
 * <p>
 * 主要是更新微件信息
 */
public class WidgetUtil {
    /**
     * 播放时
     *
     * @param context
     */
    public static void onPlaying(Context context) {
        RemoteViews views = getMusicWidgetRemoteViews(context);

        views.setImageViewResource(R.id.play, R.drawable.music_pause);

        update(context, views);
    }

    /**
     * 暂停时
     *
     * @param context
     */
    public static void onPaused(Context context) {
        RemoteViews views = getMusicWidgetRemoteViews(context);

        views.setImageViewResource(R.id.play, R.drawable.music_play);

        update(context, views);
    }

    /**
     * 准备完成了
     *
     * @param context
     * @param data
     * @param icon
     */
    public static void onPrepared(final Context context, Song data, Bitmap icon) {
        final RemoteViews views = getMusicWidgetRemoteViews(context);

        views.setTextViewText(R.id.title, String.format("%s - %s", data.getTitle(), data.getSinger().getNickname()));
        views.setProgressBar(R.id.progress, (int) data.getDuration(), (int) data.getProgress(), false);

        //显示封面
        views.setImageViewBitmap(R.id.icon, icon);
        update(context, views);
    }

    /**
     * 播放进度改变时
     *
     * @param context
     */
    public static void onProgress(Context context, Song data) {
        RemoteViews views = getMusicWidgetRemoteViews(context);

        views.setProgressBar(R.id.progress, (int) data.getDuration(), (int) data.getProgress(), false);

        update(context, views);
    }

    /**
     * 桌面歌词显示状态改变了
     *
     * @param isShow
     */
    public static void onGlobalLyricShowStatusChanged(Context context, boolean isShow) {
        RemoteViews contentView = getMusicWidgetRemoteViews(context);

        contentView.setImageViewResource(R.id.lyric, isShow ? R.drawable.ic_lyric : R.drawable.ic_lyric_selected);

        update(context, contentView);
    }

    /**
     * 更新组件
     *
     * @param context
     * @param views
     */
    private static void update(Context context, RemoteViews views) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        ComponentName componentName = new ComponentName(context, MusicWidget.class);
        manager.updateAppWidget(componentName, views);
    }


    /**
     * 获取music微件
     *
     * @param context
     * @return
     */
    private static RemoteViews getMusicWidgetRemoteViews(Context context) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.music_widget);
        return remoteViews;
    }
}
