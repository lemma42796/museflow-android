package com.ixuea.courses.mymusic.component.product.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.PagingData;
import androidx.paging.rxjava3.PagingRx;

import com.ixuea.courses.mymusic.component.product.model.Product;
import com.ixuea.courses.mymusic.component.product.repository.ProductRepository;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.core.Flowable;
import kotlinx.coroutines.CoroutineScope;


/**
 * 商品界面ViewModel
 */
@HiltViewModel
public class ProductViewModel extends ViewModel {
    /**
     * 数据仓库
     */
    private ProductRepository repository;

    CoroutineScope viewModelScope = ViewModelKt.getViewModelScope(this);

    /**
     * 当前搜索关键字
     */
    private String query;

    /**
     * 排序方法，取值查看ProductService.products方法注释
     */
    private int order;

    /**
     * 通过这个变量控制外界加载数据
     */
    private MutableLiveData<Long> _loadData = new MutableLiveData<>();
    public LiveData<Long> loadData = _loadData;

    /**
     * 打开详情界面
     */
    private MutableLiveData<Integer> _detail = new MutableLiveData<>();
    public MutableLiveData<Integer> detail = _detail;

    /**
     * 根据品牌搜索
     */
    private String brand;

    @Inject
    public ProductViewModel(ProductRepository repository) {
        this.repository = repository;
    }

    /**
     * 商品列表
     *
     * @return
     */
    public Flowable<PagingData<Product>> products() {
//        io.reactivex.rxjava3.core.Observable<ListResponse<Product>> result = repository.products(1, null, null)
//                //在子线程中执行
//                .subscribeOn(Schedulers.io())
//
//                //结果在主线程中观察
//                .observeOn(AndroidSchedulers.mainThread());

        Flowable<PagingData<Product>> result = repository.products(order, query, brand);
        PagingRx.cachedIn(result, viewModelScope);

        return result;
    }

    /**
     * 设置查询关键字
     *
     * @param query
     */
    public void setQuery(String query) {
        this.query = query;

        setLoadData();
    }

    private void setLoadData() {
        _loadData.setValue(System.currentTimeMillis());
    }

    /**
     * 设置排序
     *
     * @param data
     */
    public void setSort(int data) {
        this.order = data;
        setLoadData();
    }

    /**
     * 品牌筛选改变了
     *
     * @param data
     */
    public void setBrand(List<String> data) {
        if (data == null) {
            this.brand = null;
        } else {
            //多个品牌拼接为空格传递，当然如果服务端需要列表，也可以直接传递列表
            this.brand = StringUtils.join(data, ",");
        }
        setLoadData();

    }

    /**
     * 点击了商品列表的商品，跳转到详情
     *
     * @param data
     */
    public void detail(int data) {
        _detail.setValue(data);
    }
}
