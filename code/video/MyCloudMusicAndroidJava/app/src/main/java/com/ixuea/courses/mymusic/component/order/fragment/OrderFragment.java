package com.ixuea.courses.mymusic.component.order.fragment;

import static autodispose2.AutoDispose.autoDisposable;

import android.os.Bundle;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.component.api.HttpObserver;
import com.ixuea.courses.mymusic.component.order.activity.OrderDetailActivity;
import com.ixuea.courses.mymusic.component.order.adapter.OrderAdapter;
import com.ixuea.courses.mymusic.component.order.model.Order;
import com.ixuea.courses.mymusic.databinding.RecyclerViewBinding;
import com.ixuea.courses.mymusic.fragment.BaseViewModelFragment;
import com.ixuea.courses.mymusic.model.Base;
import com.ixuea.courses.mymusic.model.response.DetailResponse;
import com.ixuea.courses.mymusic.model.response.ListResponse;
import com.ixuea.courses.mymusic.repository.DefaultRepository;
import com.ixuea.courses.mymusic.util.Constant;
import com.ixuea.superui.dialog.SuperDialog;

import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;

/**
 * 订单界面
 */
public class OrderFragment extends BaseViewModelFragment<RecyclerViewBinding> {

    private int status;
    private OrderAdapter adapter;

    public static OrderFragment newInstance(int status) {

        Bundle args = new Bundle();
        args.putInt(Constant.STYLE, status);

        OrderFragment fragment = new OrderFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initDatum() {
        super.initDatum();
        status = extraInt(Constant.STYLE);

        adapter = new OrderAdapter(R.layout.item_order);
        binding.list.setAdapter(adapter);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        adapter.setOnItemClickListener((adapter, view, position) -> {
            Order data = (Order) adapter.getItem(position);
            startActivityExtraId(OrderDetailActivity.class, data.getId());
        });

        adapter.addChildClickViewIds(R.id.cancel_order);
        adapter.setOnItemChildClickListener((adapter, view, position) -> {
            Order data = (Order) adapter.getItem(position);
            SuperDialog.newInstance(getChildFragmentManager())
                    .setTitleRes(R.string.confirm_cancel_order)
                    .setOnClickListener(v -> cancelOrder(data)).show();

        });
    }

    private void cancelOrder(Order data) {
        DefaultRepository.getInstance()
                .cancelOrder(data.getId())
                .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new HttpObserver<DetailResponse<Base>>() {
                    @Override
                    public void onSucceeded(DetailResponse<Base> d) {
                        //重新加载数据
                        loadData();
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    protected void loadData(boolean isPlaceholder) {
        super.loadData(isPlaceholder);
        DefaultRepository.getInstance()
                .orders(status)
                .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new HttpObserver<ListResponse<Order>>() {
                    @Override
                    public void onSucceeded(ListResponse<Order> data) {
                        adapter.setNewInstance(data.getData().getData());
                    }
                });
    }
}
