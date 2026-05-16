package com.ixuea.courses.mymusic.util;

import android.app.Activity;

import com.alipay.sdk.app.PayTask;
import com.ixuea.courses.mymusic.component.pay.model.alipay.PayResult;
import com.ixuea.courses.mymusic.component.pay.model.event.AlipayStatusChangedEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

/**
 * 支付工具类
 */
public class PayUtil {
    private static final String TAG = "PayUtil";

    /**
     * 支付宝支付
     * 支付宝官方开发文档：https://docs.open.alipay.com/204/105295/
     *
     * @param activity
     * @param data
     */
    public static void alipay(Activity activity, String data) {
        //创建运行对象
        Runnable runnable = new Runnable() {
            /**
             * 子线程执行
             */
            @Override
            public void run() {
                //创建支付宝支付任务
                PayTask alipay = new PayTask(activity);

                //支付结果
                Map<String, String> result = alipay.payV2(data, true);

                //解析支付结果
                PayResult resultData = new PayResult(result);

                //发布状态
                EventBus.getDefault().post(new AlipayStatusChangedEvent(resultData));
            }
        };

        //创建一个线程
        Thread thread = new Thread(runnable);

        //启动线程
        thread.start();
    }
}
