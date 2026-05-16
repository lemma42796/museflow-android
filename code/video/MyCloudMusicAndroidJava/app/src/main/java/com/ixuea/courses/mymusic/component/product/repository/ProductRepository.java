package com.ixuea.courses.mymusic.component.product.repository;

import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.rxjava3.PagingRx;

import com.ixuea.courses.mymusic.component.product.api.ProductService;
import com.ixuea.courses.mymusic.component.product.datasource.ProductDatasource;
import com.ixuea.courses.mymusic.component.product.model.Product;
import com.ixuea.courses.mymusic.model.response.DetailResponse;
import com.ixuea.courses.mymusic.util.Constant;

import io.reactivex.rxjava3.core.Flowable;

/**
 * 商品仓库
 */
public class ProductRepository {
    private ProductService service;

    public ProductRepository(ProductService service) {
        this.service = service;
    }

    /**
     * 商品列表
     *
     * @return
     */
    public Flowable<PagingData<Product>> products(int order,
                                                  String query,
                                                  String brand) {
//        return service.products(1, 10, order, query, brand)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
        PagingConfig pagingConfig = new PagingConfig(
                //每页加载数量
                Constant.DEFAULT_SIZE,

                //距离底部（下拉就是顶部）多少条数据时，开始加载更多，如果设置为0，表示要滑动到底部（顶部）才加载
                //默认为 每页加载数量，例如每页加载10条，这个参数也设置为10，表示滚动到还剩10条数据没显示时就开始加载更多
                //如果用户网络比较慢，还想用户无感知，那可以设置大一点，例如：5 * 每页加载数量
                Constant.DEFAULT_SIZE,

                false,

                //初始化加载多少条，默认为 每页加载数量*3
                Constant.DEFAULT_SIZE * 3
        );

        //Pager 对象会调用 PagingSource 对象的 load() 方法，为其提供 LoadParams 对象，并接收 LoadResult 对象作为交换
        Pager pager = new Pager(
                pagingConfig,
                null,
                null,
                () -> new ProductDatasource(service, order, query, brand)

//                (Function0<PagingSource>) () -> new ProductDatasource(service, order, query, brand)

//        new Function0<PagingSource>() {
//            @Override
//            public PagingSource invoke() {
//                return new ProductDatasource(service, order, query, brand);
//            }
//        }

        );

        Flowable flowable = PagingRx.getFlowable(pager);
        return flowable;
    }

    /**
     * 商品详情
     *
     * @param data
     * @return
     */
    public Flowable<DetailResponse<Product>> productDetail(String data) {
        return service.productDetail(data);
    }
}
