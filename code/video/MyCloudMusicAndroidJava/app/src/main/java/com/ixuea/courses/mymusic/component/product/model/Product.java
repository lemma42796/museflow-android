package com.ixuea.courses.mymusic.component.product.model;

import com.ixuea.courses.mymusic.model.Common;

import java.util.Objects;

/**
 * 商品
 */
public class Product extends Common {
    /**
     * 标题
     */
    private String title;

    /**
     * 封面
     * <p>
     * 多张图片地址以英文逗号分割存储
     * 也可以像动态那样单独存储，这里之所以讲解这种方式
     * 是因为单独存储动态那边讲解了，同时像以逗号分割这种方式
     * 在真实项目中也挺常用
     */
    private String icon;

    /**
     * 价格
     */
    private Double price;

    /**
     * 亮点
     * <p>
     * 真实项目中，如果有特别需求，也可以实现为列表
     * 我们这里主要讲解商城核心逻辑，所以这里就简单实现了
     */
    private String highlight;

    /**
     * 详情
     * <p>
     * HTML格式化富文本
     */
    private String detail;

    /**
     * 已经付款的购买人数
     */
    private long ordersCount;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getHighlight() {
        return highlight;
    }

    public void setHighlight(String highlight) {
        this.highlight = highlight;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public long getOrdersCount() {
        return ordersCount;
    }

    public void setOrdersCount(long ordersCount) {
        this.ordersCount = ordersCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Product product = (Product) o;
        return ordersCount == product.ordersCount && title.equals(product.title) && icon.equals(product.icon) && price.equals(product.price) && Objects.equals(highlight, product.highlight) && Objects.equals(detail, product.detail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), title, icon, price, highlight, detail, ordersCount);
    }

    /**
     * 将icon字段以逗号分割为数组
     *
     * @return
     */
    public String[] getIcons() {
        return icon.split(",");
    }
}
