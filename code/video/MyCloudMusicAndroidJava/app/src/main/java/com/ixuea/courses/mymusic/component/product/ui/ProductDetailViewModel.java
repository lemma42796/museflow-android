package com.ixuea.courses.mymusic.component.product.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ixuea.courses.mymusic.component.cart.model.Cart;
import com.ixuea.courses.mymusic.component.cart.repository.ShopCartRepository;
import com.ixuea.courses.mymusic.component.product.model.Product;
import com.ixuea.courses.mymusic.component.product.repository.ProductRepository;
import com.ixuea.courses.mymusic.model.response.DetailResponse;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * 商品详情界面ViewModel
 */
@HiltViewModel
public class ProductDetailViewModel extends ViewModel {

    private final ShopCartRepository cartRepository;
    private ProductRepository repository;
    private String id;

    /**
     * 打开登录界面
     */
    private MutableLiveData<Void> _loginPage = new MutableLiveData<>();
    public LiveData<Void> loginPage = _loginPage;

    /**
     * 打开购买界面
     */
    private MutableLiveData<String> _purchasePage = new MutableLiveData<>();
    public LiveData<String> purchasePage = _purchasePage;

    /**
     * 添加到购物车成功
     */
    private MutableLiveData<Long> _addToCartSuccess = new MutableLiveData<>();
    public LiveData<Long> addToCartSuccess = _addToCartSuccess;

    @Inject
    public ProductDetailViewModel(ProductRepository repository, ShopCartRepository cartRepository) {
        this.repository = repository;
        this.cartRepository = cartRepository;
    }

    public Flowable<DetailResponse<Product>> productDetail(String id) {
        this.id = id;
        Flowable<DetailResponse<Product>> result = repository.productDetail(id)
                //在子线程中执行
                .subscribeOn(Schedulers.io())

                //结果在主线程中观察
                .observeOn(AndroidSchedulers.mainThread());

        return result;
    }

    /**
     * 打开购买界面
     */
    public void startPurchasePage() {
        _purchasePage.setValue(id);
    }

    /**
     * 打开登录界面
     */
    public void startLoginPage() {
        _loginPage.setValue(null);
    }

    /**
     * 添加到购物车
     */
    public void addCart() {
        Cart param = new Cart();
        param.setProductId(id);
        cartRepository.addProductToCart(param)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    _addToCartSuccess.setValue(System.currentTimeMillis());
                });
        ;
    }

}
