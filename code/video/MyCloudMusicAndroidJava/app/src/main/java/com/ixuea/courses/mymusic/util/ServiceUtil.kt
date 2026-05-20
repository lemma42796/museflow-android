package com.ixuea.courses.mymusic.util

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build

/**
 * 服务相关工具类
 */
object ServiceUtil {
    /**
     * 启动service
     */
    @JvmStatic
    fun startService(context: Context, clazz: Class<*>) {
        if (!isServiceRunning(context, clazz)) {
            val intent = Intent(context, clazz)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }

    /**
     * 启动service
     */
    @JvmStatic
    fun startService(context: Context, intent: Intent) {
        context.startService(intent)
    }

    /**
     * service是否在运行
     */
    @JvmStatic
    fun isServiceRunning(context: Context, clazz: Class<*>): Boolean {
        val activityManager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val services = activityManager.getRunningServices(Int.MAX_VALUE)

        if (services == null || services.isEmpty()) {
            return false
        }

        for (service in services) {
            if (service.service.className == clazz.name) {
                return true
            }
        }

        return false
    }

    /**
     * 当前应用是否后台运行
     */
    @JvmStatic
    fun isBackgroundRunning(context: Context): Boolean {
        val activityManager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses

        for (appProcess in appProcesses) {
            if (appProcess.processName == context.packageName) {
                return appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
            }
        }

        return false
    }
}
