package com.ixuea.courses.mymusic.component.pay.model.response;

/**
 * 支付参数
 * 网络请求响应
 */
public class PayResponse {
    /**
     * 支付渠道
     */
    private int channel;

    /**
     * 支付宝参数
     */
    private String pay;

    /**
     * 微信支付参数
     */
    private WechatPay wechatPay;

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public String getPay() {
        return pay;
    }

    public void setPay(String pay) {
        this.pay = pay;
    }

    public WechatPay getWechatPay() {
        return wechatPay;
    }

    public void setWechatPay(WechatPay wechatPay) {
        this.wechatPay = wechatPay;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PayResponse{");
        sb.append("channel=").append(channel);
        sb.append(", pay='").append(pay).append('\'');
        sb.append(", wechatPay=").append(wechatPay);
        sb.append('}');
        return sb.toString();
    }
}
