package com.ixuea.courses.mymusic.component.pay.model.response;

import com.google.gson.annotations.SerializedName;

/**
 * 微信支付参数（服务端返回）
 * <p>
 * 字段解释：https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=9_1
 */
public class WechatPay {
    private String appid;
    private String partnerid;
    private String prepayid;
    private String noncestr;
    private String timestamp;

    @SerializedName("package")
    private String packageValue;

    private String sign;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getPartnerid() {
        return partnerid;
    }

    public void setPartnerid(String partnerid) {
        this.partnerid = partnerid;
    }

    public String getPrepayid() {
        return prepayid;
    }

    public void setPrepayid(String prepayid) {
        this.prepayid = prepayid;
    }

    public String getNoncestr() {
        return noncestr;
    }

    public void setNoncestr(String noncestr) {
        this.noncestr = noncestr;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getPackageValue() {
        return packageValue;
    }

    public void setPackageValue(String packageValue) {
        this.packageValue = packageValue;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
