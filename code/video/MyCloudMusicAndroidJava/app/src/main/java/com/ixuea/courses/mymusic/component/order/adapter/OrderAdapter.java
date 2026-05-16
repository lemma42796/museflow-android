package com.ixuea.courses.mymusic.component.order.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.component.cart.model.Cart;
import com.ixuea.courses.mymusic.component.order.model.Order;
import com.ixuea.courses.mymusic.component.product.model.Product;
import com.ixuea.courses.mymusic.databinding.ItemOrderProductSmallBinding;
import com.ixuea.courses.mymusic.util.ImageUtil;
import com.ixuea.courses.mymusic.util.SuperDateUtil;

/**
 * 订单列表适配器
 */
public class OrderAdapter extends BaseQuickAdapter<Order, BaseViewHolder> {
    public OrderAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, Order data) {
        holder.setText(R.id.date, SuperDateUtil.yyyyMMddHHmmss(data.getCreatedAt()));

        //状态
        holder.setText(R.id.status, data.getStatusFormat());

        //状态颜色
        holder.setTextColor(R.id.status, getContext()
                .getColor(data.getStatusColor()));

        //总计
        holder.setText(R.id.total, getContext().getString(R.string.total_count, data.getProducts().size()));

        //还需支付
        holder.setText(R.id.total_price, getContext().getString(R.string.price, data.getPrice()));

        //待支付时显示取消订单按钮
        holder.setGone(R.id.cancel_order, !data.isWaitPay());

        //待发货时显示修改地址按钮
        holder.setGone(R.id.change_address, !data.isShipped());

        ViewGroup productContainer = holder.getView(R.id.product_container);
        productContainer.removeAllViews();

        //显示商品
        Product product;
        ItemOrderProductSmallBinding itemBinding;
        for (Cart it : data.getProducts()) {
            itemBinding = ItemOrderProductSmallBinding.inflate(LayoutInflater.from(getContext()),
                    productContainer, false);

            product = it.getProduct();

            //图标
            ImageUtil.show(getContext(), itemBinding.icon, product.getIcons()[0]);

            //标题
            itemBinding.title.setText(product.getTitle());

            //价格
            String price = getContext().getResources().getString(R.string.price, product.getPrice());
            itemBinding.price.setText(price);

            //数量
            itemBinding.count.setText(getContext().getResources().getString(R.string.product_count, it.getCount()));

            productContainer.addView(itemBinding.getRoot());
        }
    }
}
