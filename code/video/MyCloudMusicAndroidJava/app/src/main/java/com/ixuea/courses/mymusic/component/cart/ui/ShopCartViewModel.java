package com.ixuea.courses.mymusic.component.cart.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ixuea.courses.mymusic.component.cart.model.Cart;
import com.ixuea.courses.mymusic.component.cart.repository.ShopCartRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * 购物车界面ViewModel
 */
@HiltViewModel
public class ShopCartViewModel extends ViewModel {
    private final ShopCartRepository repository;

    /**
     * 数据请求成功
     * <p>
     * 如果要实现分页，可以按照商品列表那样实现
     * 这里就不再实现了
     */
    private MutableLiveData<List<Cart>> _loadDataSuccess = new MutableLiveData<>();
    public LiveData<List<Cart>> loadDataSuccess = _loadDataSuccess;

    /**
     * 编辑成功
     */
    private MutableLiveData<Long> _editSuccess = new MutableLiveData<>();
    public LiveData<Long> editSuccess = _editSuccess;

    /**
     * 删除购物车成功
     */
    private MutableLiveData<Long> _deleteSuccess = new MutableLiveData<>();
    public LiveData<Long> deleteSuccess = _deleteSuccess;

    @Inject
    public ShopCartViewModel(ShopCartRepository repository) {
        this.repository = repository;
    }

    public void loadData() {
        repository.carts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    _loadDataSuccess.setValue(result.getData().getData());
                });
    }

    public void editCart(Cart data) {
        repository.editCart(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    _editSuccess.setValue(System.currentTimeMillis());
                });
    }

    /**
     * 删除购物车（项）
     *
     * @return
     */
    public void deleteCarts(List<String> selectCarts) {
        repository.deleteCarts(selectCarts)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    _deleteSuccess.setValue(System.currentTimeMillis());
                });
    }
}
