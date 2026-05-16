package com.ixuea.courses.mymusic.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.ixuea.courses.mymusic.MainActivity;
import com.ixuea.courses.mymusic.service.MusicPlayerService;

/**
 * intent相关工具类
 */
public class IntentUtil {
    /**
     * 将原来的intent上的自定义信息拷贝到新的intent
     * <p>
     * 主要是实现这样的功能
     * 例如：点击推送后会启动 启动界面并携带一些数据，而这些数据需要完全传递到MainActivity使用
     * 所以需要从启动界面的intent中获取并设置到启动主界面的intent
     *
     * @param oldIntent
     * @param intent
     * @return
     */
    public static void cloneIntent(Intent oldIntent, Intent intent) {
        if (!Intent.ACTION_MAIN.equals(oldIntent.getAction())) {
            //获取原来的信息，通常是应用中启动 该界面设置的，例如：MusicControlReceiver中实现的点击封面启动应用
            intent.setAction(oldIntent.getAction());

            intent.putExtra(Constant.PUSH, oldIntent.getStringExtra(Constant.PUSH));

            intent.putExtra(Constant.STYLE, oldIntent.getIntExtra(Constant.STYLE, 0));

            intent.putExtra(Constant.SHEET_ID, oldIntent.getStringExtra(Constant.SHEET_ID));
        }
    }

    /**
     * 创建启动主界面的PendingIntent
     *
     * @param context
     * @param action
     * @return
     */
    public static PendingIntent createMainActivityPendingIntent(Context context, String action) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(action);

        return PendingIntent.getActivity(context,
                action.hashCode(),
                intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
    }

    /**
     * 创建启动MusicPlayerService的PendingIntent
     *
     * @param context
     * @param data
     * @return
     */
    public static PendingIntent createMusicPlayerServicePendingIntent(Context context, String data) {
        return PendingIntent.getService(context, data.hashCode(), createMusicPlayerServiceIntent(context, data), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
    }

    /**
     * 创建启动MusicPlayerService
     *
     * @param context
     * @param data
     * @return
     */
    public static Intent createMusicPlayerServiceIntent(Context context, String data) {
        Intent intent = new Intent(context, MusicPlayerService.class);
        intent.setAction(data);
        return intent;
    }

    /**
     * 使用系统浏览器打开
     *
     * @param data
     */
    public static void startBrowser(Context context, String data) {
        Uri uri = Uri.parse(data);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }
}
