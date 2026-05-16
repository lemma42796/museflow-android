package com.ixuea.superui.util;

import android.content.Context;
import android.content.Intent;

import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.graphics.drawable.IconCompat;

import com.ixuea.courses.mymusic.component.splash.activity.SplashActivity;

/**
 * 快捷方式工具类
 */
public class SuperShortcutUtil {
    /**
     * 添加快捷方法
     *
     * @param context
     * @param title
     * @param icon
     * @param action
     * @param rank    排名，越小越靠前
     */
    public static void addShortcut(Context context, int title, int icon, String action, int rank) {
        //播放本地音乐
        Intent intent = new Intent(context, SplashActivity.class);
        intent.setAction(action);
        ShortcutInfoCompat shortcut = new ShortcutInfoCompat.Builder(context, action)
                .setShortLabel(context.getString(title))
                .setIcon(IconCompat.createWithResource(context, icon))
                .setIntent(intent)
                .setRank(rank)
                .build();

        ShortcutManagerCompat.pushDynamicShortcut(context, shortcut);
    }
}
