package com.ixuea.superui.util;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Process;

import java.util.List;

/**
 * 线程工具类
 */
public class SuperThreadUtil {
    /**
     * 是否是主线程
     *
     * @param context
     * @return
     */
    public static boolean isMainThread(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processes = activityManager.getRunningAppProcesses();
        String mainProcessName = context.getApplicationInfo().processName;
        int myPid = Process.myPid();
        for (ActivityManager.RunningAppProcessInfo data : processes) {
            if (data.pid == myPid && mainProcessName.equals(data.processName)) {
                return true;
            }
        }
        return false;
    }

}
