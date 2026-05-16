package com.ixuea.superui.util;

import android.os.Handler;
import android.os.Looper;

/**
 * 延迟工具类
 */
public class SuperDelayUtil {
    /**
     * 任务
     */
    private Runnable runnable;

    private static Handler handler = new Handler(Looper.getMainLooper());

    /**
     * 延时后执行
     *
     * @param data     毫秒，如果小于等于0，马上执行
     * @param listener
     */
    public static void delay(long data, SuperDelayListener listener) {
        if (data <= 0) {
            listener.onRun();
            return;
        }
        handler.postDelayed(() -> listener.onRun(), data);
    }

    public void throttle(int delayMillisecond, SuperDelayListener listener) {
        //取消原来的任务
        if (runnable != null) {
            handler.removeCallbacks(runnable);
            runnable = null;
        }

        //创建新的任务
        runnable = () -> innerRun(listener);

        //500毫秒后执行
        handler.postDelayed(runnable, delayMillisecond);
    }

    private void innerRun(SuperDelayListener listener) {
        listener.onRun();
    }

    public interface SuperDelayListener {
        void onRun();
    }
}
