package com.ixuea.courses.mymusic.component.order.model;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.component.address.model.Address;
import com.ixuea.courses.mymusic.component.cart.model.Cart;
import com.ixuea.courses.mymusic.model.Common;
import com.ixuea.courses.mymusic.util.Constant;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 订单模型
 */
public class Order extends Common {
    /**
     * 订单状态
     */
    private int status;

    /**
     * 商品总价
     */
    private Double totalPrice;

    /**
     * 价格
     */
    private double price;

    /**
     * 订单来源
     * 一般订单来源不会返回给客户端
     * 我们这样返回来只是给大家演示如何显示这些字段而已
     * 也就是在那个平台创建的订单
     */
    private int source;

    /**
     * 支付来源
     * 因为可能有创建订单是web网站
     * 用户是在手机上支付的
     */
    private int origin;

    /**
     * 支付渠道
     */
    private int channel;

    /**
     * 订单号
     */
    private String number;

    /**
     * 订单所关联的商品信息
     */
    private List<Cart> products;

    /**
     * 地址
     */
    private Address address;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public int getOrigin() {
        return origin;
    }

    public void setOrigin(int origin) {
        this.origin = origin;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public List<Cart> getProducts() {
        return products;
    }

    public void setProducts(List<Cart> products) {
        this.products = products;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    //region 辅助方法

    /**
     * 是否已经支付成功
     *
     * @return
     */
    public boolean isPaid() {
        return status >= Constant.WAIT_SHIPPED;
    }

    /**
     * 是否是待支付
     *
     * @return
     */
    public boolean isWaitPay() {
        return status == Constant.WAIT_PAY;
    }

    /**
     * 是否待发货
     *
     * @return
     */
    public boolean isShipped() {
        return status == Constant.WAIT_SHIPPED;
    }

    /**
     * 支付渠道格式化
     *
     * @return
     */
    public String getChannelFormat() {
        switch (channel) {
            case Constant.ALIPAY:
                return "支付宝";
            case Constant.WECHAT:
                return "微信";
            case Constant.HUABEI_STAGE:
                return "花呗分期";
            default:
                return "";
        }
    }

    /**
     * 支付来源格式化
     *
     * @return
     */
    public String getOriginFormat() {
        return sourceFormat(origin);
    }

    /**
     * 来源格式化
     *
     * @param data
     * @return
     */
    @NotNull
    private String sourceFormat(int data) {
        switch (data) {
            case Constant.ANDROID:
                return "Android";
            case Constant.IOS:
                return "iOS";
            case Constant.WEB:
                return "Web";
            default:
                return "";
        }
    }

    /**
     * 订单来源格式化
     *
     * @return
     */
    public String getSourceFormat() {
        return sourceFormat(source);
    }

    /**
     * 获取订单状态
     *
     * @return
     */
    public int getStatusFormat() {
        switch (status) {
            case Constant.CLOSE:
                return R.string.order_close;
            case Constant.WAIT_SHIPPED:
                return R.string.wait_shipped;
            case Constant.WAIT_RECEIVED:
                return R.string.wait_received;
            case Constant.WAIT_COMMENT:
                return R.string.wait_comment;
            case Constant.COMPLETE:
                return R.string.complete;
            default:
                return R.string.wait_pay;
        }
    }

    /**
     * 获取状态颜色
     *
     * @return
     */
    public int getStatusColor() {
        switch (status) {
            case Constant.COMPLETE:
                return R.color.pass;
            default:
                return R.color.black80;
        }
    }
    //endregion
}
