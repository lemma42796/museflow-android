package com.ixuea.courses.mymusic.component.pay.model.request;

import com.ixuea.courses.mymusic.util.Constant;

/**
 * 请求支付信息参数
 */
public class PayRequest {
    /**
     * 支付平台
     * 默认值为android
     * 且不能更改
     * 因为Android平台的来说肯定就是Android
     */
    private final int origin = Constant.ANDROID;

    /**
     * 支付渠道
     */
    private int channel;

    public int getOrigin() {
        return origin;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }
}
