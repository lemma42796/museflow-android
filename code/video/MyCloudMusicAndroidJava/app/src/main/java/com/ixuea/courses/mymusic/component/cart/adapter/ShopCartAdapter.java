package com.ixuea.courses.mymusic.component.cart.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.component.cart.model.Cart;
import com.ixuea.courses.mymusic.util.ImageUtil;

import java.util.Iterator;

/**
 * 购物车列表adapter
 */
public class ShopCartAdapter extends BaseQuickAdapter<Cart, BaseViewHolder> {
    private boolean selectAll;

    public ShopCartAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, Cart data) {
        //选择状态
        holder.setImageResource(R.id.select_icon, data.isSelect() ? R.drawable.radio_button_checked : R.drawable.radio_button);

        ImageUtil.show(getContext(), holder.getView(R.id.icon), data.getProduct().getIcons()[0]);

        holder.setText(R.id.title, data.getProduct().getTitle());

        //价格
        String price = getContext().getResources().getString(R.string.price, data.getProduct().getPrice());
        holder.setText(R.id.price, price);

        //数量
        holder.setText(R.id.count, String.valueOf(data.getCount()));
    }

    /**
     * 切换选中所有
     */
    public void toggleSelectAll() {
        selectAll = !selectAll;
        for (Cart it : getData()) {
            it.setSelect(selectAll);
        }

        notifyItemRangeChanged(0, getItemCount());
    }

    public boolean isSelectAll() {
        return selectAll;
    }

    public void deleteSelect() {
        Iterator<Cart> iterator = getData().iterator();
        while (iterator.hasNext()) {
            if (iterator.next().isSelect()) {
                iterator.remove();
            }
        }
        notifyDataSetChanged();
    }
}
