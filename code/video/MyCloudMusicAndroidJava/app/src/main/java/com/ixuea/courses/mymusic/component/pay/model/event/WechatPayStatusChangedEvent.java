package com.ixuea.courses.mymusic.component.pay.model.event;

import com.tencent.mm.opensdk.modelbase.BaseResp;

/**
 * 微信支付状态改变了事件
 */
public class WechatPayStatusChangedEvent {

    /**
     * 支付状态
     */
    private BaseResp data;

    public WechatPayStatusChangedEvent(BaseResp data) {
        this.data = data;
    }

    public BaseResp getData() {
        return data;
    }

    public void setData(BaseResp data) {
        this.data = data;
    }
}
