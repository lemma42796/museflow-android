package com.ixuea.courses.mymusic.component.address.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.component.address.model.Address;

/**
 * 收货地址适配器
 */
public class AddressAdapter extends BaseQuickAdapter<Address, BaseViewHolder> {
    public AddressAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, Address data) {
        holder.setVisible(R.id.default_address, data.isDefault());
        holder.setText(R.id.contact, data.getContact());

        holder.setText(R.id.area, data.getReceiverArea());
        holder.setText(R.id.detail, data.getDetail());
    }
}
