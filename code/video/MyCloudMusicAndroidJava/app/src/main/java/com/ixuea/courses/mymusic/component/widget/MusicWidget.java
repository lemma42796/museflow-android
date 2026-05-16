package com.ixuea.courses.mymusic.component.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.component.song.model.Song;
import com.ixuea.courses.mymusic.manager.MusicListManager;
import com.ixuea.courses.mymusic.service.MusicPlayerService;
import com.ixuea.courses.mymusic.util.Constant;
import com.ixuea.courses.mymusic.util.IntentUtil;
import com.ixuea.courses.mymusic.util.ResourceUtil;
import com.ixuea.courses.mymusic.util.ServiceUtil;

/**
 * 音乐桌面微件
 * <p>
 * https://developer.android.google.cn/guide/topics/appwidgets
 */
public class MusicWidget extends AppWidgetProvider {
    /**
     * 添加，重新运行应用，周期时间，都会调用
     *
     * @param context
     * @param appWidgetManager
     * @param appWidgetIds
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        //尝试启动service
        ServiceUtil.startService(context.getApplicationContext(), MusicPlayerService.class);

        //获取播放列表管理器
        MusicListManager musicListManager = MusicPlayerService.getListManager(context.getApplicationContext());

        //获取当前播放的音乐
        final Song data = musicListManager.getData();

        final int N = appWidgetIds.length;
        // 循环处理每一个，因为桌面上可能添加多个
        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];

            // 创建远程控件，所有对view的操作都必须通过该view提供的方法
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.music_widget);

            //因为这是在桌面的控件里面显示我们的控件，所以不能直接通过setOnClickListener设置监听器
            //这里发送的动作在MusicReceiver处理
            PendingIntent iconPendingIntent = IntentUtil.createMainActivityPendingIntent(context, Constant.ACTION_MUSIC_PLAYER_PAGE);

            //这里直接启动service，也可以用广播接收
            PendingIntent previousPendingIntent = IntentUtil.createMusicPlayerServicePendingIntent(context, Constant.ACTION_PREVIOUS);
            PendingIntent playPendingIntent = IntentUtil.createMusicPlayerServicePendingIntent(context, Constant.ACTION_PLAY);
            PendingIntent nextPendingIntent = IntentUtil.createMusicPlayerServicePendingIntent(context, Constant.ACTION_NEXT);
            PendingIntent lyricPendingIntent = IntentUtil.createMusicPlayerServicePendingIntent(context, Constant.ACTION_LYRIC);

            //设置点击事件
            views.setOnClickPendingIntent(R.id.icon, iconPendingIntent);
            views.setOnClickPendingIntent(R.id.previous, previousPendingIntent);
            views.setOnClickPendingIntent(R.id.play, playPendingIntent);
            views.setOnClickPendingIntent(R.id.next, nextPendingIntent);
            views.setOnClickPendingIntent(R.id.lyric, lyricPendingIntent);

            if (data == null) {
                //当前没有播放音乐
                appWidgetManager.updateAppWidget(appWidgetId, views);
            } else {
                //有播放音乐
                views.setTextViewText(R.id.title, String.format("%s - %s", data.getTitle(), data.getSinger().getNickname()));
                views.setProgressBar(R.id.progress, (int) data.getDuration(), (int) data.getProgress(), false);

                //显示图标
                RequestOptions options = new RequestOptions();
                options.centerCrop();
                Glide.with(context)
                        .asBitmap()
                        .load(ResourceUtil.resourceUri(data.getIcon()))
                        .apply(options)
                        .into(new CustomTarget<Bitmap>() {

                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                //显示封面
                                views.setImageViewBitmap(R.id.icon, resource);
                                appWidgetManager.updateAppWidget(appWidgetId, views);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                //显示默认图片
                                views.setImageViewBitmap(R.id.icon, BitmapFactory.decodeResource(context.getResources(), R.drawable.placeholder));
                                appWidgetManager.updateAppWidget(appWidgetId, views);
                            }
                        });
            }
        }
    }
}
