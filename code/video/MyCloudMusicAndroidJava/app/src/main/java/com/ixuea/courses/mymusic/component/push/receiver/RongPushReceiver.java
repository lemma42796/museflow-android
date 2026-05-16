package com.ixuea.courses.mymusic.component.push.receiver;

import android.content.Context;
import android.content.Intent;

import com.ixuea.courses.mymusic.component.push.model.Push;
import com.ixuea.courses.mymusic.component.push.model.PushMessage;
import com.ixuea.courses.mymusic.component.splash.activity.SplashActivity;
import com.ixuea.courses.mymusic.util.Constant;
import com.ixuea.courses.mymusic.util.JSONUtil;

import io.rong.push.PushType;
import io.rong.push.notification.PushMessageReceiver;
import io.rong.push.notification.PushNotificationMessage;
import timber.log.Timber;

/**
 * 自定义融云推送
 * 主要是实现点击通知跳转对应的界面
 */
public class RongPushReceiver extends PushMessageReceiver {
    /**
     * 推送点击
     *
     * @param context
     * @param pushType
     * @param notificationMessage
     * @return
     */
    @Override
    public boolean onNotificationMessageClicked(Context context, PushType pushType, PushNotificationMessage notificationMessage) {
        Timber.d("onNotificationMessageClicked %s %s", pushType.getName(), notificationMessage.getPushData());

        // 可通过 pushType 判断 Push 的类型
        // 华为 和 Oppo的推送点击事件不会回调到该方法, 需要按照各自的配置方法在相应的activity解析intent获得信息.
        if (pushType == PushType.XIAOMI) {
            process(context, notificationMessage.getPushData());
            return true; // 此处返回 true. 代表不触发 SDK 默认实现，您自定义处理通知点击跳转事件。
        }
        return super.onNotificationMessageClicked(context, pushType, notificationMessage);
    }

    private void process(Context context, String param) {
        Push push = JSONUtil.fromJSON(param, Push.class);
        switch (push.getStyle()) {
            case Push.PUSH_STYLE_CHAT:
                //聊天消息
                processChatPush(context, push.getMessage());
                break;
            default:
                Timber.w("process unknown push style.");
                break;
        }
    }

    private void processChatPush(Context context, PushMessage data) {
        //实现您自定义的通知点击跳转逻辑
        Intent intent = new Intent(context, SplashActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        //添加push信息，字段统一命名为push
        intent.putExtra(Constant.PUSH, data.getUserId());

        intent.setAction(Constant.ACTION_PUSH);

        context.startActivity(intent);
    }
}
