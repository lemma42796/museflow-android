package com.ixuea.courses.mymusic.component.cart.repository;

import com.ixuea.courses.mymusic.component.cart.api.ShopCartService;
import com.ixuea.courses.mymusic.component.cart.model.Cart;
import com.ixuea.courses.mymusic.model.Base;
import com.ixuea.courses.mymusic.model.response.DetailResponse;
import com.ixuea.courses.mymusic.model.response.ListResponse;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;

/**
 * 购物车仓库
 */
public class ShopCartRepository {
    private final ShopCartService service;

    public ShopCartRepository(ShopCartService service) {
        this.service = service;
    }

    /**
     * 添加到购物车
     *
     * @param data
     * @return
     */
    public Flowable<DetailResponse<Base>> addProductToCart(Cart data) {
        return service.addProductToCart(data);
    }

    /**
     * 购物车列表
     *
     * @return
     */
    public Flowable<ListResponse<Cart>> carts() {
        return service.carts();
    }

    /**
     * 编辑购物车
     *
     * @return
     */
    public Flowable<DetailResponse<Base>> editCart(Cart data) {
        return service.editCart(data.getId(), data);
    }

    /**
     * 删除购物车（项）
     *
     * @param data
     * @return
     */
    public Flowable<DetailResponse<Base>> deleteCarts(List<String> data) {
        return service.deleteCarts(data);
    }
}
