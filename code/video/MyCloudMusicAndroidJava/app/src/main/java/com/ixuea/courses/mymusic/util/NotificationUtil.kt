package com.ixuea.courses.mymusic.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import com.ixuea.courses.mymusic.AppContext
import com.ixuea.courses.mymusic.MainActivity
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.component.sheet.activity.SheetDetailActivity
import com.ixuea.courses.mymusic.manager.UserManager
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation

/**
 * 通知相关工具类
 */
object NotificationUtil {
    private const val CHANNEL_ID_DEFAULT = "CHANNEL_ID_DEFAULT"
    private const val CHANNEL_ID_MESSAGE = "CHANNEL_ID_MESSAGE"
    const val CHANNEL_ID_MUSIC: String = "CHANNEL_ID_MUSIC"

    private var notificationManager: NotificationManager? = null

    /**
     * 显示简单提示通知。
     */
    @JvmStatic
    fun showAlert(message: Int) {
        val manager = getNotificationManager()
        val context = AppContext.getInstance()

        createNotificationChannel(CHANNEL_ID_DEFAULT, context.getString(R.string.channel_default))

        val intent = Intent(context, SheetDetailActivity::class.java).apply {
            putExtra(Constant.ID, "1")
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_DEFAULT)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(context.getString(message))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
            .setContentIntent(contentPendingIntent)
            .setAutoCancel(true)
            .build()

        manager.notify("showAlert".hashCode(), notification)
    }

    @JvmStatic
    fun notify(id: Int, notification: Notification) {
        getNotificationManager().notify(id, notification)
    }

    private fun createNotificationChannel(id: String, data: String) {
        createNotificationChannel(id, data, NotificationManager.IMPORTANCE_DEFAULT)
    }

    private fun createNotificationChannel(id: String, data: String, importance: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(id, data, importance).apply {
                setShowBadge(true)
                enableLights(true)
            }
            getNotificationManager().createNotificationChannel(channel)
        }
    }

    /**
     * 获取通知管理器。
     */
    private fun getNotificationManager(): NotificationManager {
        if (notificationManager == null) {
            notificationManager = AppContext.getInstance()
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        return notificationManager!!
    }

    /**
     * 获取一个设置 service 为前台的通知。
     */
    @JvmStatic
    fun getServiceForeground(context: Context): Notification {
        createNotificationChannel(
            CHANNEL_ID_MUSIC,
            AppContext.getInstance().getString(R.string.channel_music)
        )

        return NotificationCompat.Builder(context, CHANNEL_ID_MUSIC)
            .setContentTitle("")
            .setContentText("")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
            .build()
    }

    /**
     * 显示解锁全局歌词通知。
     */
    @JvmStatic
    fun showUnlockGlobalLyricNotification(context: Context) {
        createNotificationChannel(CHANNEL_ID_DEFAULT, context.getString(R.string.channel_default))

        val contentPendingIntent = PendingIntent.getBroadcast(
            context,
            Constant.ACTION_UNLOCK_LYRIC.hashCode(),
            Intent(Constant.ACTION_UNLOCK_LYRIC),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID_DEFAULT)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(context.resources.getString(R.string.lock_lyric_title))
            .setContentText(context.resources.getString(R.string.lock_lyric_content))
            .setContentIntent(contentPendingIntent)

        notify(Constant.NOTIFICATION_UNLOCK_LYRIC_ID, builder.build())
    }

    /**
     * 清除通知。
     */
    @JvmStatic
    @Suppress("UNUSED_PARAMETER")
    fun clearUnlockGlobalLyricNotification(_context: Context) {
        getNotificationManager().cancel(Constant.NOTIFICATION_UNLOCK_LYRIC_ID)
    }

    /**
     * 显示消息通知。
     */
    @JvmStatic
    fun showMessage(content: String, targetId: String) {
        AppContext.getInstance().chatClient.getUnreadCount(
            Conversation.ConversationType.PRIVATE,
            targetId,
            object : RongIMClient.ResultCallback<Int>() {
                override fun onSuccess(unreadCount: Int?) {
                    UserManager.getInstance(AppContext.getInstance())
                        .getUser(targetId) { userData ->
                            val userId = userData.id.orEmpty()
                            showMessage(
                                content,
                                unreadCount ?: 0,
                                userId,
                                MessageUtil.getNickname(userId, userData.nickname)
                            )
                        }
                }

                override fun onError(errorCode: RongIMClient.ErrorCode?) {
                }
            }
        )
    }

    private fun showMessage(content: String, unreadCount: Int, targetId: String, nickname: String) {
        val context = AppContext.getInstance()
        val intent = Intent(context, MainActivity::class.java).apply {
            action = Constant.ACTION_CHAT
            putExtra(Constant.ID, targetId)
        }

        val displayContent = if (unreadCount > 1) {
            context.resources.getString(
                R.string.message_notification_count,
                unreadCount,
                content
            )
        } else {
            content
        }

        createNotificationChannel(
            CHANNEL_ID_MESSAGE,
            context.getString(R.string.channel_chat_message),
            NotificationManager.IMPORTANCE_HIGH
        )

        val contentPendingIntent = PendingIntent.getActivity(
            context,
            Constant.ACTION_CHAT.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID_MESSAGE)
            .setAutoCancel(true)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setNumber(unreadCount)
            .setContentTitle(nickname)
            .setContentText(displayContent)
            .setWhen(System.currentTimeMillis())
            .setContentIntent(contentPendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        }

        getNotificationManager().notify(targetId.hashCode(), builder.build())
    }
}
