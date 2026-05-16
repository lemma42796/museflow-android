package com.ixuea.courses.mymusic.component.order.model.request;

import com.ixuea.courses.mymusic.model.Base;
import com.ixuea.courses.mymusic.util.Constant;

import java.util.ArrayList;

/**
 * 创建订单参数
 * 可以复用Order
 */
public class OrderRequest extends Base {
    /**
     * 商品id
     */
    private String productId;

    /**
     * 从购物车，选中item购买
     */
    private ArrayList<String> carts;

    /**
     * 地址id
     * <p>
     * 用来客户端传递地址id
     */
    private String addressId;

    /**
     * 创建订单的平台
     * 默认值为android
     * 且不能更改
     * 因为Android平台的来说肯定就是Android
     */
    private int source = Constant.ANDROID;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public ArrayList<String> getCarts() {
        return carts;
    }

    public void setCarts(ArrayList<String> carts) {
        this.carts = carts;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }
}
