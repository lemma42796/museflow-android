package com.ixuea.courses.mymusic.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.List;

/**
 * 服务相关工具类
 */
public class ServiceUtil {
    /**
     * 启动service
     *
     * @param context
     * @param clazz
     */
    public static void startService(Context context, Class<?> clazz) {
        if (!ServiceUtil.isServiceRunning(context, clazz)) {
            //创建Intent
            Intent intent = new Intent(context, clazz);

            //启动服务
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent);
            } else {
                context.startService(intent);
            }
        }
    }

    /**
     * 启动service
     *
     * @param context
     * @param intent
     */
    public static void startService(Context context, Intent intent) {
        context.startService(intent);
    }

    /**
     * service是否在运行
     *
     * @param context
     * @param clazz
     * @return
     */
    public static boolean isServiceRunning(Context context, Class<?> clazz) {
        //获取活动管理器
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        //获取所有运行的服务
        List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

        if (services == null || services.size() == 0) {
            //没有找到运行的服务
            return false;
        }

        //遍历所有服务
        //查看是否有我们需要的服务
        for (ActivityManager.RunningServiceInfo service : services) {
            if (service.service.getClassName().equals(clazz.getName())) {
                //找到了
                return true;
            }
        }

        return false;
    }

    /**
     * 当前应用是否后台运行
     *
     * @param context
     * @return
     */
    public static boolean isBackgroundRunning(Context context) {
        //获取活动管理器
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        //获取所有运行的进程
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();

        //遍历所有进程
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses
        ) {
            //进程名等于我们应用的包名
            if (appProcess.processName.equals(context.getPackageName())) {
                //找到了我们当前应用
                return appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
            }
        }

        //默认就是前台
        return false;
    }
}
