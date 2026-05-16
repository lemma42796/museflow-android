package com.ixuea.superui.process;

import android.os.Process;

/**
 * 进程工具类
 */
public class SuperProcessUtil {
    /**
     * 杀死当前应用
     */
    public static void killApp() {
        Process.killProcess(Process.myPid());
    }
}
