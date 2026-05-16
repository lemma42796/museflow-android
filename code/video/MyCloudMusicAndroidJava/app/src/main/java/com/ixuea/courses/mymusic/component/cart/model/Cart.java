package com.ixuea.courses.mymusic.component.cart.model;

import com.ixuea.courses.mymusic.component.product.model.Product;
import com.ixuea.courses.mymusic.model.Common;

/**
 * 购物车item
 */
public class Cart extends Common {
    /**
     * 商品
     */
    private Product product;

    /**
     * 数量
     */
    private int count;

    /**
     * 添加到购物车时，使用
     */
    private String productId;

    /**
     * 是否选中
     */
    private boolean select;

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    /**
     * 切换选择
     */
    public void toggleSelect() {
        select = !select;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }
}
