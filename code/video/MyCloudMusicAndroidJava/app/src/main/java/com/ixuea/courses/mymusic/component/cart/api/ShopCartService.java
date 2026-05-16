package com.ixuea.courses.mymusic.component.cart.api;

import com.ixuea.courses.mymusic.component.cart.model.Cart;
import com.ixuea.courses.mymusic.model.Base;
import com.ixuea.courses.mymusic.model.response.DetailResponse;
import com.ixuea.courses.mymusic.model.response.ListResponse;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * 购物车API
 * <p>
 * 并不是一定要用Retrofit，也可以直接使用HttpURLConnect，或者OkHttp
 * 只是在Android这边目前主流就是用Retrofit
 */
public interface ShopCartService {

    /**
     * 购物车列表
     *
     * @return
     */
    @GET("v1/carts")
    Flowable<ListResponse<Cart>> carts();

    /**
     * 编辑购物车
     *
     * @return
     */
    @PATCH("v1/carts/{id}")
    Flowable<DetailResponse<Base>> editCart(@Path("id") String id, @Body Cart data);

    /**
     * 添加到购物车
     *
     * @param id
     * @return
     */
    @POST("v1/carts")
    Flowable<DetailResponse<Base>> addProductToCart(@Body Cart id);

    /**
     * 批量删除购物车（项）
     *
     * @param data
     * @return
     */
    @POST("v1/carts/batch_delete")
    Flowable<DetailResponse<Base>> deleteCarts(@Body List<String> data);
}
