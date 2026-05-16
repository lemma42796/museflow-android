package com.ixuea.courses.mymusic.component.order.activity;

import android.content.Context;
import android.content.Intent;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.activity.BaseTitleActivity;
import com.ixuea.courses.mymusic.component.address.activity.AddressActivity;
import com.ixuea.courses.mymusic.component.address.model.event.AddressChangedEvent;
import com.ixuea.courses.mymusic.component.api.HttpObserver;
import com.ixuea.courses.mymusic.component.cart.model.Cart;
import com.ixuea.courses.mymusic.component.order.model.request.OrderRequest;
import com.ixuea.courses.mymusic.component.order.model.response.ConfirmOrderResponse;
import com.ixuea.courses.mymusic.component.pay.activity.PayActivity;
import com.ixuea.courses.mymusic.component.product.model.Product;
import com.ixuea.courses.mymusic.databinding.ActivityConfirmOrderBinding;
import com.ixuea.courses.mymusic.databinding.ItemOrderProductBinding;
import com.ixuea.courses.mymusic.model.BaseId;
import com.ixuea.courses.mymusic.model.response.DetailResponse;
import com.ixuea.courses.mymusic.repository.DefaultRepository;
import com.ixuea.courses.mymusic.util.Constant;
import com.ixuea.courses.mymusic.util.ImageUtil;
import com.ixuea.superui.toast.SuperToast;
import com.ixuea.superui.util.SuperViewUtil;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import autodispose2.AutoDispose;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;

/**
 * 确认订单界面
 */
public class ConfirmOrderActivity extends BaseTitleActivity<ActivityConfirmOrderBinding> {

    private String id;
    private OrderRequest param;
    private ConfirmOrderResponse data;

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    /**
     * 购物车进入
     *
     * @param context
     */
    public static void startWithCarts(Context context, ArrayList<String> carts) {
        Intent intent = new Intent(context, ConfirmOrderActivity.class);
        intent.putExtra(Constant.DATA, carts);
        context.startActivity(intent);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        //地址容器点击
        binding.address.getRoot().setOnClickListener(v -> AddressActivity.startWithSelect(getHostActivity()));

        binding.primary.setOnClickListener(v -> {
            //创建订单，创建成功后，跳转到支付界面
            createOrder();
        });
    }

    private void createOrder() {
        if (StringUtils.isBlank(param.getAddressId())) {
            SuperToast.show(R.string.select_address);
            return;
        }

        DefaultRepository.getInstance()
                .createOrder(param)
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new HttpObserver<DetailResponse<BaseId>>() {
                    @Override
                    public void onSucceeded(DetailResponse<BaseId> data) {
                        PayActivity.startFromConfirmOrder(getHostActivity(), data.getData().getId());
                        finish();
                    }
                });
    }

    @Override
    protected void loadData(boolean isPlaceholder) {
        super.loadData(isPlaceholder);
        DefaultRepository.getInstance()
                .confirmOrder(param)
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new HttpObserver<DetailResponse<ConfirmOrderResponse>>() {
                    @Override
                    public void onSucceeded(DetailResponse<ConfirmOrderResponse> data) {
                        showData(data.getData());
                    }
                });
    }

    private void showData(ConfirmOrderResponse data) {
        this.data = data;

        //地址
        if (data.getAddress() == null) {
            SuperViewUtil.gone(binding.address.topAddressContainer);
            binding.address.detail.setText(R.string.select_address);
        } else {
            SuperViewUtil.show(binding.address.topAddressContainer);

            SuperViewUtil.show(binding.address.defaultAddress, data.getAddress().isDefault());
            binding.address.contact.setText(data.getAddress().getContact());

            binding.address.area.setText(data.getAddress().getReceiverArea());
            binding.address.detail.setText(data.getAddress().getDetail());

            //设置到创建订单参数对象
            param.setAddressId(data.getAddress().getId());
        }

        //显示商品
        binding.productContainer.removeAllViews();

        Product product;
        ItemOrderProductBinding itemBinding;
        for (Cart it : data.getCarts()) {
            itemBinding = ItemOrderProductBinding.inflate(getLayoutInflater(), this.binding.productContainer, false);

            product = it.getProduct();

            //图标
            ImageUtil.show(getHostActivity(), itemBinding.icon, product.getIcons()[0]);

            //标题
            itemBinding.title.setText(product.getTitle());

            //价格
            String price = getResources().getString(R.string.price, product.getPrice());
            itemBinding.price.setText(price);

            //数量
            itemBinding.count.setText(getResources().getString(R.string.product_count, it.getCount()));

            binding.productContainer.addView(itemBinding.getRoot());
        }

        //商品总价
        binding.totalPrice.setText(getResources().getString(R.string.price, data.getTotalPrice()));

        binding.total.setText(getResources().getString(R.string.total_count, data.getCarts().size()));

        //还需支付
        binding.price.setText(getResources().getString(R.string.price, data.getPrice()));
    }

    /**
     * 更改了收货地址事件
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void addressChangedEvent(AddressChangedEvent event) {
        param.setAddressId(event.getData().getId());

        //重新调用服务端计算，因为真实项目中有些商品，不同的收货地址，邮费不一样
        loadData();
    }

    @Override
    protected void initDatum() {
        super.initDatum();
        id = extraId();

        ArrayList<String> carts = getIntent().getStringArrayListExtra(Constant.DATA);

        param = new OrderRequest();

        //设置商品id
        param.setProductId(id);

        //设置购物车id
        param.setCarts(carts);

        loadData();
    }
}