package com.ixuea.courses.mymusic.service;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media.session.MediaButtonReceiver;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.component.lyric.view.GlobalLyricView;
import com.ixuea.courses.mymusic.component.song.model.Song;
import com.ixuea.courses.mymusic.manager.MusicListManager;
import com.ixuea.courses.mymusic.manager.MusicPlayerListener;
import com.ixuea.courses.mymusic.manager.MusicPlayerManager;
import com.ixuea.courses.mymusic.manager.impl.GlobalLyricManagerImpl;
import com.ixuea.courses.mymusic.manager.impl.MusicListManagerImpl;
import com.ixuea.courses.mymusic.manager.impl.MusicPlayerManagerImpl;
import com.ixuea.courses.mymusic.util.Constant;
import com.ixuea.courses.mymusic.util.NotificationUtil;
import com.ixuea.courses.mymusic.util.ResourceUtil;
import com.ixuea.courses.mymusic.util.WidgetUtil;

import timber.log.Timber;

/**
 * 音乐播放service
 */
public class MusicPlayerService extends Service implements MusicPlayerListener, GlobalLyricView.GlobalLyricOtherListener {
    /**
     * 通知id
     */
    private static final int NOTIFICATION_ID = 100;

    /**
     * 能接收的操作
     */
    private static final long MEDIA_SESSION_ACTIONS =
            PlaybackStateCompat.ACTION_PLAY
                    | PlaybackStateCompat.ACTION_PAUSE
                    | PlaybackStateCompat.ACTION_PLAY_PAUSE
                    | PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                    | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                    | PlaybackStateCompat.ACTION_STOP
                    | PlaybackStateCompat.ACTION_SEEK_TO;

    /**
     * 媒体会话
     */
    private MediaSessionCompat mediaSession;
    private MusicListManager musicListManager;
    /**
     * 媒体回调
     */
    private MediaSessionCompat.Callback callback = new MediaSessionCompat.Callback() {
        @Override
        public void onPlay() {
            musicListManager.resume();
        }

        @Override
        public void onPause() {
            musicListManager.pause();
        }

        @Override
        public void onSkipToNext() {
            musicListManager.play(musicListManager.next());
        }

        @Override
        public void onSkipToPrevious() {
            musicListManager.play(musicListManager.previous());
        }

        @Override
        public void onSeekTo(long pos) {
            musicListManager.seekTo((int) pos);
        }
    };
    private MusicPlayerManager musicPlayerManager;
    private long lastWidgetProgressTime;

    /**
     * 全局桌面歌词管理器
     */
    private GlobalLyricManagerImpl globalLyricManager;


    /**
     * 获取音乐播放Manager
     * <p>
     * 为什么不直接将逻辑写到Service呢？
     * 是因为操作service要么通过bindService
     * 那么startService来使用
     * 比较麻烦
     *
     * @return
     */
    public static MusicPlayerManager getMusicPlayerManager(Context context) {
        return MusicPlayerManagerImpl.getInstance(context);
    }

    /**
     * 获取列表管理器
     *
     * @param context
     * @return
     */
    public static MusicListManager getListManager(Context context) {
        return MusicListManagerImpl.getInstance(context);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.d("onCreate");

        //因为这个API是8.0才有的
        //所以要这样判断版本
        //不然低版本会崩溃
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //设置service为前台service
            //提高应用的优先级

            //获取通知
            Notification notification = NotificationUtil.getServiceForeground(getApplicationContext());

            //Id写0：这个通知就不会显示
            //对于我们这里来说
            //就需要不显示
            startForeground(12, notification);
        }

        musicListManager = MusicListManagerImpl.getInstance(getApplicationContext());
        musicPlayerManager = MusicPlayerManagerImpl.getInstance(getApplicationContext());
        globalLyricManager = GlobalLyricManagerImpl.getInstance(getApplicationContext());
        musicPlayerManager.addMusicPlayerListener(this);

        globalLyricManager.setGlobalLyricOtherListener(this);

        initMediaSession();

        Song data = musicListManager.getData();
        if (data != null) {
            Glide.with(getApplicationContext()).asBitmap().load(ResourceUtil.resourceUri(data.getIcon())).into(new CustomTarget<Bitmap>() {

                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    updateMetaData(data, resource);
                    if (musicPlayerManager.isPlaying()) {
                        onPlaying(data);
                    } else {
                        onPaused(data);
                    }
                    musicPlayerManager.prepareLyric(data);
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {
                    updateMetaData(data, BitmapFactory.decodeResource(getResources(), R.drawable.placeholder));
                    showMusicNotification();
                }
            });
        }
    }

    /**
     * 初始化媒体会话
     */
    private void initMediaSession() {
        mediaSession = new MediaSessionCompat(getApplicationContext(), "MusicListManager");

        //设置监听回调
        mediaSession.setCallback(callback);

        //必须设置为true，这样才能开始接收各种信息
        mediaSession.setActive(true);
    }

    /**
     * 更新状态
     *
     * @param state    状态
     * @param position 当前播放进度
     * @param mediaId  媒体id
     */
    private void updatePlaybackState(@PlaybackStateCompat.State int state, int position, int mediaId) {
        PlaybackStateCompat.Builder builder =
                new PlaybackStateCompat.Builder()
                        //设置能支持的操作
                        .setActions(MEDIA_SESSION_ACTIONS)

                        //设置媒体id
                        .setActiveQueueItemId(mediaId)

                        //设置状态
                        .setState(state, position, 1.0f);

        mediaSession.setPlaybackState(builder.build());
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 启动service调用
     * <p>
     * 多次启动也调用该方法
     *
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timber.d("onStartCommand");
        if (intent == null) {
            return super.onStartCommand(intent, flags, startId);
        }

        if (Constant.ACTION_PREVIOUS.equals(intent.getAction())) {
            //上一曲
            callback.onSkipToPrevious();
        } else if (Constant.ACTION_PLAY.equals(intent.getAction())) {
            //播放按钮
            if (musicPlayerManager.isPlaying()) {
                callback.onPause();
            } else {
                callback.onPlay();
            }
        } else if (Constant.ACTION_NEXT.equals(intent.getAction())) {
            //下一曲
            callback.onSkipToNext();
        } else if (Constant.ACTION_LYRIC.equals(intent.getAction())) {
            //桌面歌词
            if (globalLyricManager.isShowing()) {
                globalLyricManager.hide();
            } else {
                globalLyricManager.show();
            }

            showMusicNotification();
        } else {
            MediaButtonReceiver.handleIntent(mediaSession, intent);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 销毁时调用
     */
    @Override
    public void onDestroy() {
        //停止前台服务
        //true:移除之前的通知
        stopForeground(true);

        super.onDestroy();
    }

    @Override
    public void onPaused(Song data) {
        int index = musicListManager.getDatum().indexOf(data);
        updatePlaybackState(PlaybackStateCompat.STATE_PAUSED, (int) data.getProgress(), index);
        showMusicNotification();

        WidgetUtil.onPaused(getApplicationContext());
    }

    @Override
    public void onPlaying(Song data) {
        int index = musicListManager.getDatum().indexOf(data);
        updatePlaybackState(PlaybackStateCompat.STATE_PLAYING, (int) data.getProgress(), index);
        showMusicNotification();

        //更新Widget
        WidgetUtil.onPlaying(getApplicationContext());
    }

    @Override
    public void onProgress(Song data) {
        int index = musicListManager.getDatum().indexOf(data);
        updatePlaybackState(PlaybackStateCompat.STATE_PLAYING, (int) data.getProgress(), index);

        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - lastWidgetProgressTime > Constant.SAVE_PROGRESS_TIME) {
            WidgetUtil.onProgress(getApplicationContext(), data);
            lastWidgetProgressTime = currentTimeMillis;
        }
    }

    /**
     * 显示媒体通知
     */
    private void showMusicNotification() {
        Notification notification = NotificationUtil.createMusicNotification(getApplicationContext(), musicPlayerManager.isPlaying(), globalLyricManager.isShowing(), mediaSession);
        if (notification == null) {
            return;
        }

        //因为这个API是8.0才有的
        //所以要这样判断版本
        //不然低版本会崩溃
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(NOTIFICATION_ID, notification);
        } else {
            NotificationUtil.notify(NOTIFICATION_ID, notification);
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp, Song data) {
        //加载封面
        Glide.with(getApplicationContext()).asBitmap().load(ResourceUtil.resourceUri(data.getIcon())).into(new CustomTarget<Bitmap>() {

            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                updateMetaData(data, resource);
                showMusicNotification();

                WidgetUtil.onPrepared(getApplicationContext(), data, resource);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
                Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.placeholder);
                updateMetaData(data, icon);
                showMusicNotification();

                WidgetUtil.onPrepared(getApplicationContext(), data, icon);
            }
        });
    }

    /**
     * 更新媒体信息
     *
     * @param data
     * @param icon
     */
    public void updateMetaData(Song data, Bitmap icon) {
        MediaMetadataCompat.Builder metaData = new MediaMetadataCompat.Builder()
                //标题
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, data.getTitle())

                //艺术家，也就是歌手
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, data.getSinger().getNickname())

                //专辑
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, "专辑")

                //专辑艺术家
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, "专辑艺术家")

                //时长
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, data.getDuration())

                //封面
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, icon);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //播放列表长度
            metaData.putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, musicListManager.getDatum().size());
        }

        mediaSession.setMetadata(metaData.build());
    }

    @Override
    public void closeLyric() {
        globalLyricManager.hide();
        showMusicNotification();
    }
}
