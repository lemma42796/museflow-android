package com.ixuea.courses.mymusic.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.ixuea.courses.mymusic.AppContext;
import com.ixuea.courses.mymusic.component.pay.model.event.WechatPayStatusChangedEvent;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

import org.greenrobot.eventbus.EventBus;

import timber.log.Timber;

/**
 * 微信支付回调界面
 */
public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
    private static final String TAG = "WXPayEntryActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView textView = new TextView(this);
        setContentView(textView);

        processIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        processIntent(intent);
    }

    /**
     * 处理微信回调结果
     *
     * @param intent
     */
    private void processIntent(Intent intent) {
        AppContext.getInstance().getWxapi().handleIntent(intent, this);
    }

    /**
     * 请求微信，会回调该方法
     *
     * @param baseReq
     */
    @Override
    public void onReq(BaseReq baseReq) {
    }

    /**
     * 微信回调
     *
     * @param baseResp
     */
    @Override
    public void onResp(BaseResp baseResp) {
        Timber.d("onResp %d %d %s", baseResp.getType(), baseResp.errCode, baseResp.errStr);

        if (baseResp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            //支付回调

            //发送通知，支付界面处理
            EventBus.getDefault().post(new WechatPayStatusChangedEvent(baseResp));
            finish();
        }
    }
}