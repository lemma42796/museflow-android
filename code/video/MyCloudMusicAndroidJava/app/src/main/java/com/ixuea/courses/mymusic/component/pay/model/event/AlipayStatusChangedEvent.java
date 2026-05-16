package com.ixuea.courses.mymusic.component.pay.model.event;


import com.ixuea.courses.mymusic.component.pay.model.alipay.PayResult;

/**
 * 支付宝支付状态改变了事件
 */
public class AlipayStatusChangedEvent {
    /**
     * 支付状态
     */
    private PayResult data;

    public AlipayStatusChangedEvent(PayResult data) {
        this.data = data;
    }

    public PayResult getData() {
        return data;
    }

    public void setData(PayResult data) {
        this.data = data;
    }
}
