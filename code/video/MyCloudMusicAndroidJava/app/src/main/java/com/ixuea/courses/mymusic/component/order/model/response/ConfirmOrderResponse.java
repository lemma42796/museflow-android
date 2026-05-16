package com.ixuea.courses.mymusic.component.order.model.response;

import com.ixuea.courses.mymusic.component.address.model.Address;
import com.ixuea.courses.mymusic.component.cart.model.Cart;
import com.ixuea.courses.mymusic.model.Base;

import java.util.List;

/**
 * 确认订单响应模型
 */
public class ConfirmOrderResponse extends Base {
    /**
     * 商品总价
     * <p>
     * 像更改了收货地址（可能产生不同的运费），选择优惠券等情况，本地不要计算，一切都要在服务端计算
     */
    private Double totalPrice;

    /**
     * 还需支付价格
     * <p>
     * 减去优惠券+邮费 后的价格
     */
    private Double price;

    /**
     * 用户传递地址id后，返回的地址对象
     */
    private Address address;

    /**
     * 购物车item
     * <p>
     * 如果是在订单详情购买一个商品，那列表只要一个，并且cart没有id
     */
    private List<Cart> carts;

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<Cart> getCarts() {
        return carts;
    }

    public void setCarts(List<Cart> carts) {
        this.carts = carts;
    }
}
