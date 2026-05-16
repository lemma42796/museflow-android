package com.ixuea.courses.mymusic.component.product.datasource;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagingState;
import androidx.paging.rxjava3.RxPagingSource;

import com.ixuea.courses.mymusic.component.product.api.ProductService;
import com.ixuea.courses.mymusic.component.product.model.Product;
import com.ixuea.courses.mymusic.exception.ResponseException;
import com.ixuea.courses.mymusic.model.response.ListResponse;
import com.ixuea.courses.mymusic.util.Constant;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * 商品数据源
 * <p>
 * 这里使用Jetpack中的分页组件实现分页
 * <p>
 * 对于数据流（网络，数据库获取的数据）用Kotlin推荐继承PagingSource，返回推荐为Flow，因为LiveData设计的比较简单
 * 处理错误不是很方便，但像从ViewModel中传递简单的数据，例如：更改界面标题，推荐LiveData
 * <p>
 * Java可以用RxJava，Guava/LiveData，这里用RxJava
 * <p>
 * https://developer.android.com/topic/libraries/architecture/paging/v3-paged-data#java
 */
public class ProductDatasource extends RxPagingSource<Integer, Product> {
    private final ProductService service;
    private final int order;
    private final String query;
    private final String brand;

    /**
     * @param service 网络数据源
     * @param query   搜索关键词，如果没有就是空
     */
    public ProductDatasource(ProductService service, int order, String query, String brand) {
        this.service = service;
        this.order = order;
        this.query = query;
        this.brand = brand;
    }

    /**
     * 调用adapter.refresh时调用
     * 返回要获取的页数
     *
     * @param pagingState
     * @return
     */
    @Nullable
    @Override
    public Integer getRefreshKey(@NonNull PagingState<Integer, Product> pagingState) {
        return null;
    }

    /**
     * 加载数据调用
     * <p>
     * https://developer.android.google.cn/reference/kotlin/androidx/paging/rxjava3/RxPagingSource
     *
     * @param loadParams
     * @return
     */
    @NonNull
    @Override
    public Single<LoadResult<Integer, Product>> loadSingle(@NonNull LoadParams<Integer> loadParams) {
        Integer page = loadParams.getKey();
//        Integer page = 10;

        if (page == null) {
            //默认第一页

            //如果不传递，我们后端API默认为1，只是这里为了客户端代码完整就传递
            page = Constant.DEFAULT_PAGE;
        }

        Integer finalPage = page;

        return service.products(page, loadParams.getLoadSize(), order, query, brand)
                .subscribeOn(Schedulers.io())
                //数据转换，正常返回数据
                //返回200，status!=0时，就是业务错误，也会走到这里
                .map(new Function<ListResponse<Product>, LoadResult<Integer, Product>>() {
                    @Override
                    public LoadResult<Integer, Product> apply(ListResponse<Product> result) throws Throwable {
                        //判断业务请求是否成功
                        if (!result.isSucceeded()) {
                            return new LoadResult.Error(ResponseException.create(result));
                        }

                        //计算下一页
                        Integer nextKey = null;
                        if (result.getData().getData() == null || result.getData().getPage() == result.getData().getPages()) {
                            //如果列表没有数据（例如搜索，筛选后），或者当前页数等于总页数，就没有下一页数据了
                        } else {
                            //之所以要这样计算，是因为默认第一次会加载Constant.DEFAULT_SIZE*3的数据
                            //第二次加载不能加载前面重复的数据，所以要这样计算
                            //例如：每页加载10条，那第一次就加载了30条，第二次加载就要从第4页开始加载
                            nextKey = finalPage + (loadParams.getLoadSize() / Constant.DEFAULT_SIZE);
                        }

                        List<Product> results = result.getData().getData();
                        if (results == null) {
                            results = new ArrayList<>();
                        }

                        LoadResult.Page pageResult = new LoadResult.Page(
                                results,

                                //上一页，以为我们这里不支持向前翻页，所以传递null
                                null,

                                //下一页
                                nextKey
                        );

                        return pageResult;
                    }
                })

                //返回错误时返回，包括请求网络错误，本地参数错误，解析响应错误等
                .onErrorReturn(e -> {
                    //在这里判断具体的错误，并进一步处理，方便外界使用
                    //当然也可以直接返回异常，外界处理
//                        if (e instanceof HttpException) {
//                            return new LoadResult.Error<>(e);
//                        }

                    return new LoadResult.Error<>(e);
                });
    }
}
