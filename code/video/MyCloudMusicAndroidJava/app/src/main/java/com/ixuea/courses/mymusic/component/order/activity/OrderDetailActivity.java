package com.ixuea.courses.mymusic.component.order.activity;

import static autodispose2.AutoDispose.autoDisposable;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.activity.BaseTitleActivity;
import com.ixuea.courses.mymusic.component.api.HttpObserver;
import com.ixuea.courses.mymusic.component.cart.model.Cart;
import com.ixuea.courses.mymusic.component.order.model.Order;
import com.ixuea.courses.mymusic.component.pay.activity.PayActivity;
import com.ixuea.courses.mymusic.component.product.model.Product;
import com.ixuea.courses.mymusic.databinding.ActivityOrderDetailBinding;
import com.ixuea.courses.mymusic.databinding.ItemOrderProductBinding;
import com.ixuea.courses.mymusic.model.response.DetailResponse;
import com.ixuea.courses.mymusic.repository.DefaultRepository;
import com.ixuea.courses.mymusic.util.ImageUtil;
import com.ixuea.courses.mymusic.util.SuperDateUtil;
import com.ixuea.superui.util.SuperViewUtil;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import org.joda.time.DateTime;

import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;

/**
 * 订单详情界面
 */
public class OrderDetailActivity extends BaseTitleActivity<ActivityOrderDetailBinding> {
    private String id;

    @Override
    protected void initViews() {
        super.initViews();
        //状态栏文字白色
        QMUIStatusBarHelper.setStatusBarDarkMode(this);

        int color = getColor(R.color.primary);
        setStatusBarColor(color);
        binding.bar.getRoot().setBackgroundColor(color);
    }

    @Override
    protected void initDatum() {
        super.initDatum();
        id = extraId();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    protected void loadData(boolean isPlaceholder) {
        super.loadData(isPlaceholder);
        DefaultRepository.getInstance()
                .orderDetail(id)
                .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new HttpObserver<DetailResponse<Order>>() {
                    @Override
                    public void onSucceeded(DetailResponse<Order> data) {
                        showData(data.getData());
                    }
                });
    }

    private void showData(Order data) {
        //订单状态
        binding.status.setText(data.getStatusFormat());

        //地址
        SuperViewUtil.show(binding.address.topAddressContainer);

        SuperViewUtil.show(binding.address.defaultAddress, data.getAddress().isDefault());
        binding.address.contact.setText(data.getAddress().getContact());

        binding.address.area.setText(data.getAddress().getReceiverArea());
        binding.address.detail.setText(data.getAddress().getDetail());

        //显示商品
        binding.productContainer.removeAllViews();

        ItemOrderProductBinding itemBinding;

        Product product;
        for (Cart it : data.getProducts()) {
            itemBinding = ItemOrderProductBinding.inflate(getLayoutInflater(), binding.productContainer, false);

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

        //还需支付
        binding.amountPay.setText(getResources().getString(R.string.price, data.getPrice()));
        binding.price.setText(binding.amountPay.getText());

        //下单时间
        binding.orderTime.setText(SuperDateUtil.yyyyMMddHHmmss(data.getCreatedAt()));

        //订单号
        binding.orderNo.setText(data.getNumber());

        //订单来源
        binding.orderSource.setText(data.getSourceFormat());

        //支付平台
        binding.payPlatform.setText(data.getOriginFormat());

        //支付渠道
        binding.payChannel.setText(data.getChannelFormat());

        if (data.isWaitPay()) {
            //待付款状态
            SuperViewUtil.show(binding.payControlContainer);
            SuperViewUtil.show(binding.payRemainingTime);

            //最后支付时间
            //订单创建时间后，15分钟内完成支付
            DateTime dateTime = new DateTime(data.getCreatedAt());

            //加15分钟
            dateTime = dateTime.plusMinutes(15);

            binding.payRemainingTime.setText(
                    getString(R.string.payment_before, dateTime.toString(SuperDateUtil.HHmmss))
            );
        } else if (data.isShipped()) {
            //待发货
            SuperViewUtil.show(binding.payWaitShipContainer);

            //显示退款菜单，这里就不再实现了，因为后面还有很多逻辑
            //查看物流，评价等，但基本上没什么难点了，就是逻辑了
        }
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        binding.primary.setOnClickListener(v -> PayActivity.startFromOrder(getHostActivity(), id));
    }
}