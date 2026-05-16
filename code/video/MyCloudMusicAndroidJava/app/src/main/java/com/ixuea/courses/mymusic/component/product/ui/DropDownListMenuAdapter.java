package com.ixuea.courses.mymusic.component.product.ui;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.util.ResourceUtil;

/**
 * 下拉菜单列表适配器
 */
public class DropDownListMenuAdapter extends BaseQuickAdapter<DropMenuItem, BaseViewHolder> {
    private int selectIndex = 0;
    private int textColor;
    private int colorPrimary;

    /**
     * 构造方法
     *
     * @param layoutResId
     */
    public DropDownListMenuAdapter(int layoutResId) {
        super(layoutResId);
    }

    /**
     * 绑定数据
     *
     * @param holder
     * @param data
     */
    @Override
    protected void convert(@NonNull BaseViewHolder holder, DropMenuItem data) {
        if (selectIndex == holder.getLayoutPosition()) {
            holder.setTextColor(R.id.title, colorPrimary);
        } else {
            holder.setTextColor(R.id.title, textColor);
        }
        holder.setText(R.id.title, data.getTitle());
    }

    public void setSelect(int data) {
        if (this.selectIndex != data) {
            notifyItemChanged(this.selectIndex);
            this.selectIndex = data;
            notifyItemChanged(this.selectIndex);
        }

    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        //这个属性在superui
        colorPrimary = ResourceUtil.getColorAttributes(getContext(), com.google.android.material.R.attr.colorPrimary);
        textColor = ResourceUtil.getColorAttributes(getContext(), com.google.android.material.R.attr.colorOnSurface);
    }
}