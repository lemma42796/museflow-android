package com.ixuea.courses.mymusic.util

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.ixuea.courses.mymusic.MainActivity
import com.ixuea.courses.mymusic.playback.PlaybackService

/**
 * intent相关工具类
 */
object IntentUtil {
    /**
     * 将原来的intent上的自定义信息拷贝到新的intent
     *
     * 主要是实现这样的功能
     * 例如：点击推送后会启动 启动界面并携带一些数据，而这些数据需要完全传递到MainActivity使用
     * 所以需要从启动界面的intent中获取并设置到启动主界面的intent
     */
    @JvmStatic
    fun cloneIntent(oldIntent: Intent, intent: Intent) {
        if (Intent.ACTION_MAIN != oldIntent.action) {
            intent.action = oldIntent.action
            intent.putExtra(Constant.PUSH, oldIntent.getStringExtra(Constant.PUSH))
            intent.putExtra(Constant.STYLE, oldIntent.getIntExtra(Constant.STYLE, 0))
            intent.putExtra(Constant.SHEET_ID, oldIntent.getStringExtra(Constant.SHEET_ID))
        }
    }

    /**
     * 创建启动主界面的PendingIntent
     */
    @JvmStatic
    fun createMainActivityPendingIntent(context: Context, action: String): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            this.action = action
        }

        return PendingIntent.getActivity(
            context,
            action.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    /**
     * 创建启动PlaybackService的PendingIntent
     */
    @JvmStatic
    fun createPlaybackServicePendingIntent(context: Context, data: String): PendingIntent {
        return PendingIntent.getService(
            context,
            data.hashCode(),
            createPlaybackServiceIntent(context, data),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    /**
     * 创建启动PlaybackService
     */
    @JvmStatic
    fun createPlaybackServiceIntent(context: Context, data: String): Intent {
        return Intent(context, PlaybackService::class.java).apply {
            action = data
        }
    }

    /**
     * 使用系统浏览器打开
     */
    @JvmStatic
    fun startBrowser(context: Context, data: String) {
        val uri = Uri.parse(data)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    }
}
