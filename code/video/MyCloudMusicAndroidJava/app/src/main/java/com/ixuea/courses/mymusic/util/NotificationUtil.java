package com.ixuea.courses.mymusic.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.media.session.MediaButtonReceiver;

import com.ixuea.courses.mymusic.AppContext;
import com.ixuea.courses.mymusic.MainActivity;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.component.sheet.activity.SheetDetailActivity;
import com.ixuea.courses.mymusic.manager.UserManager;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

/**
 * 通知相关工具类
 */
public class NotificationUtil {
    private static final String CHANNEL_ID_DEFAULT = "CHANNEL_ID_DEFAULT";
    private static final String CHANNEL_ID_MESSAGE = "CHANNEL_ID_MESSAGE";
    private static final String CHANNEL_ID_MUSIC = "CHANNEL_ID_MUSIC";
    private static NotificationManager notificationManager;

    /**
     * 显示简单提示通知
     *
     * @param message
     */
    public static void showAlert(int message) {
        //获取管理器
        getNotificationManager();

        AppContext context = AppContext.getInstance();

        //创建通知渠道
        createNotificationChannel(CHANNEL_ID_DEFAULT, context.getString(R.string.channel_default));

        //设置通知点击后启动的界面
        Intent intent = new Intent(context, SheetDetailActivity.class);
        intent.putExtra(Constant.ID, "1");

        PendingIntent contentPendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID_DEFAULT)
                //通知标题
                .setContentTitle(context.getString(R.string.app_name))

                //通知内容
                .setContentText(context.getString(message))

                //通知小图标
                .setSmallIcon(R.mipmap.ic_launcher)

                //设置大图标
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))

                .setContentIntent(contentPendingIntent)

                //点击后消失
                .setAutoCancel(true)

                //创建通知
                .build();

        notify("showAlert".hashCode(), notification);
    }

    public static void notify(int id, Notification notification) {
        //获取通知管理器
        getNotificationManager();

        notificationManager.notify(id, notification);
    }

    private static void createNotificationChannel(String id, String data) {
        createNotificationChannel(id, data, NotificationManager.IMPORTANCE_DEFAULT);
    }

    private static void createNotificationChannel(String id, String data, int importance) {
        //因为这个API是8.0才有的
        //所以要这样判断版本
        //不然低版本会崩溃
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //创建渠道
            //可以多次创建
            //但Id一样只会创建一个
            NotificationChannel channel = new NotificationChannel(id, data, importance);

            //长按桌面图标时显示此渠道的通知
            channel.setShowBadge(true);

            //桌面图标显示角标
            channel.enableLights(true);

            //创建渠道
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * 获取通知管理器
     */
    private static void getNotificationManager() {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) AppContext.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
        }
    }

    /**
     * 获取一个设置service为前台的通知
     * 他只是一个测试通知
     * 无任何实际意义
     *
     * @return
     */
    public static Notification getServiceForeground(Context context) {
        //获取通知管理器
        getNotificationManager();

        //创建通知渠道
        createNotificationChannel(CHANNEL_ID_MUSIC,
                AppContext.getInstance().getString(R.string.channel_music));

        //创建一个通知
        //内容随便写
        //通知的配置有很多
        //这里就不在讲解了
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID_MUSIC)

                //通知标题
                .setContentTitle("")

                //通知内容
                .setContentText("")

                //通知小图标
                .setSmallIcon(R.mipmap.ic_launcher)

                //设置大图标
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))

                //创建通知
                .build();

        //返回
        return notification;
    }

    /**
     * 创建媒体通知
     *
     * @param isPlaying
     * @return
     */
    public static Notification createMusicNotification(Context context, boolean isPlaying, boolean isShowLyric, MediaSessionCompat mediaSession) {
        MediaControllerCompat controller = mediaSession.getController();
        MediaMetadataCompat mediaMetadata = controller.getMetadata();
        if (mediaMetadata == null) {
            return null;
        }

        //获取管理器
        getNotificationManager();

        //创建通知渠道
        createNotificationChannel(CHANNEL_ID_MUSIC, context.getString(R.string.channel_music),NotificationManager.IMPORTANCE_LOW);

        //媒体描述对象
        MediaDescriptionCompat description = mediaMetadata.getDescription();

        //创建通知
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID_MUSIC);

        builder
                //标题
                .setContentTitle(description.getTitle())

                //文本
                .setContentText(description.getSubtitle())

                //子文本
                .setSubText(description.getDescription())

                //大图标
                .setLargeIcon(description.getIconBitmap())

                //点击通知后操作
                .setContentIntent(IntentUtil.createMainActivityPendingIntent(context, Constant.ACTION_MUSIC_PLAYER_PAGE))

                // 公开可见性
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

                // 小图标
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(ContextCompat.getColor(context, R.color.primary))

                //添加按钮
                .addAction(new NotificationCompat.Action(android.R.drawable.ic_media_previous, "previous", MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)))
                .addAction(new NotificationCompat.Action(isPlaying ? android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play, "Pause", MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_PLAY_PAUSE)))
                .addAction(new NotificationCompat.Action(android.R.drawable.ic_media_next, "next", MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_SKIP_TO_NEXT)))
                .addAction(new NotificationCompat.Action(isShowLyric ? R.drawable.ic_lyric : R.drawable.ic_lyric_selected, "lyric", IntentUtil.createMainActivityPendingIntent(context, Constant.ACTION_LYRIC)))

                //设置样式
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        //设置媒体会话信息，设置后，例如：拖拽进度条就自动回调onSeekTo方法
                        .setMediaSession(mediaSession.getSessionToken())

                        //紧凑试图，显示那些按钮
                        .setShowActionsInCompactView(1)
                );

        Notification notification = builder.build();

        return notification;
    }

    /**
     * 显示解锁全局歌词通知
     *
     * @param context
     */
    public static void showUnlockGlobalLyricNotification(Context context) {
        //获取通知管理器
        getNotificationManager();

        //创建通知渠道
        createNotificationChannel(CHANNEL_ID_DEFAULT, context.getString(R.string.channel_default));

        //点击事件
        PendingIntent contentPendingIntent = PendingIntent.getBroadcast(context,
                Constant.ACTION_UNLOCK_LYRIC.hashCode(),
                new Intent(Constant.ACTION_UNLOCK_LYRIC),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        //创建通知
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID_DEFAULT)
                //设置logo
                .setSmallIcon(R.mipmap.ic_launcher)

                //设置标题
                .setContentTitle(context.getResources().getString(R.string.lock_lyric_title))

                //设置内容
                .setContentText(context.getResources().getString(R.string.lock_lyric_content))

                //设置点击后执行的意图
                .setContentIntent(contentPendingIntent);

        //显示通知
        notify(Constant.NOTIFICATION_UNLOCK_LYRIC_ID, builder.build());
    }

    /**
     * 清除通知
     *
     * @param context
     */
    public static void clearUnlockGlobalLyricNotification(Context context) {
        //获取管理器
        getNotificationManager();

        //清除通知
        notificationManager.cancel(Constant.NOTIFICATION_UNLOCK_LYRIC_ID);
    }

    /**
     * 显示消息通知
     */
    public static void showMessage(String content, String targetId) {
        //获取会话，主要是获取未读消息数
        AppContext.getInstance().getChatClient().getUnreadCount(Conversation.ConversationType.PRIVATE, targetId, new RongIMClient.ResultCallback<Integer>() {
            @Override
            public void onSuccess(Integer integer) {
                if (true) {
                    //TODO 不是免打扰，才显示通知

                    UserManager.getInstance(AppContext.getInstance())
                            .getUser(targetId, (UserManager.UserListener) userData -> {
                                showMessage(content, integer, userData.getId(), MessageUtil.getNickname(userData.getId(), userData.getNickname()));
                            });
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
            }
        });
    }


    private static void showMessage(String content, Integer unreadCount, String targetId, String nickname) {
        //创建通知点击的PendingIntent
        //设置通知点击后启动的界面
        Intent intent = new Intent(AppContext.getInstance(), MainActivity.class);

        //传递一个动作
        intent.setAction(Constant.ACTION_CHAT);

        //添加目标会话id
        intent.putExtra(Constant.ID, targetId);

        if (unreadCount > 1) {
            content = AppContext.getInstance().getResources().getString(
                    R.string.message_notification_count,
                    unreadCount,
                    content
            );
        }

        getNotificationManager();

        //创建通知渠道
        createNotificationChannel(
                CHANNEL_ID_MESSAGE,
                AppContext.getInstance().getString(R.string.channel_chat_message),
                NotificationManager.IMPORTANCE_HIGH
        );

        PendingIntent contentPendingIntent = PendingIntent.getActivity(
                AppContext.getInstance(),
                Constant.ACTION_CHAT.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(AppContext.getInstance(), CHANNEL_ID_MESSAGE);

        //点击后消失
        builder.setAutoCancel(true)

                //小图标
                .setSmallIcon(R.mipmap.ic_launcher)

                //使用默认效果， 会根据手机当前环境播放铃声， 是否振动
                .setDefaults(NotificationCompat.DEFAULT_ALL)

                //设置通知重要程度
                .setPriority(NotificationCompat.PRIORITY_HIGH)

                //通知数量
                .setNumber(unreadCount)

                //设置标题
                //单聊：用户昵称
                .setContentTitle(nickname)

                //设置内容
                .setContentText(content)

                //设置时间
                .setWhen(System.currentTimeMillis())

                //点击后要做的事
                .setContentIntent(contentPendingIntent);

        //悬挂式Notification，5.0后显示
        //注意：大部分国内的系统，会根据他们的规则，禁用显示角标，显示悬浮通知
        //所以像下面设置后，没有效果，需要手动去打开权限
        //微信等应用默认就有，是因为厂商有白名单，而我们写的应用一般都不在白名单
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        }

        Notification notification = builder.build();

        //显示
        notificationManager.notify(targetId.hashCode(), notification);
    }
}
