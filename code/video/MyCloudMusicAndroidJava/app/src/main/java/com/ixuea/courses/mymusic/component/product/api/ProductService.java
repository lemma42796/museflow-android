package com.ixuea.courses.mymusic.component.product.api;


import com.ixuea.courses.mymusic.component.product.model.Product;
import com.ixuea.courses.mymusic.model.response.DetailResponse;
import com.ixuea.courses.mymusic.model.response.ListResponse;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * 商品API
 */
public interface ProductService {
    /**
     * 商品列表
     *
     * @param page  分页
     *              是因为传统根据page分页，可能会导致数据重复
     * @param size  每页显示多少条
     * @param order 排序；0：综合（商品新增时间倒序），默认；10：价格降序，20：价格升序，
     *              30：根据id从小到大排序，主要是方便客户端测试加载逻辑是否有问题，判断是否重复数据
     *              100: 返回参数错误，主要是方便客户端测试当该接口返回错误时，客户端是否实现正确
     * @param brand 品牌，多个品牌，用逗号分割
     * @return
     */
    @GET("v1/products")
    Single<ListResponse<Product>> products(@Query(value = "page") int page,
                                           @Query(value = "size") int size,
                                           @Query(value = "order") int order,
                                           @Query(value = "query") String query,
                                           @Query(value = "brand") String brand
    );

    /**
     * 商品详情
     *
     * @param id
     * @return
     */
    @GET("v1/products/{id}")
    Flowable<DetailResponse<Product>> productDetail(@Path("id") String id);
}
