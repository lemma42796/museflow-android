package com.ixuea.courses.mymusic.component.product.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.util.ResourceUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 下拉菜单网络适配器
 */
public class DropDownGridMenuAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    /**
     * 对应的位置是否选中
     * false:未选中，默认
     * true：选中
     */
    private boolean[] selectIndexes;
    private int colorPrimary;
    private int textColor;

    /**
     * 构造方法
     *
     * @param layoutResId
     */
    public DropDownGridMenuAdapter(int layoutResId) {
        super(layoutResId);
    }

    /**
     * 绑定数据
     *
     * @param holder
     * @param data
     */
    @Override
    protected void convert(@NonNull BaseViewHolder holder, String data) {
        if (selectIndexes != null && selectIndexes[holder.getLayoutPosition()]) {
            holder.setTextColor(R.id.title, colorPrimary);
        } else {
            holder.setTextColor(R.id.title, textColor);
        }
        holder.setText(R.id.title, data);
    }

    @Override
    public void setNewInstance(@Nullable List<String> list) {
        super.setNewInstance(list);
        selectIndexes = new boolean[list.size()];
    }

    /**
     * 清除所有选择
     */
    public void resetSelect() {
        selectIndexes = new boolean[getData().size()];
        notifyDataSetChanged();
    }

    public List<String> getSelect() {
        List<String> results = new ArrayList<>();
        for (int i = 0; i < selectIndexes.length; i++) {
            if (selectIndexes[i]) {
                results.add(getItem(i));
            }
        }
        return results;
    }

    public void setSelect(int data) {
        selectIndexes[data] = !selectIndexes[data];
        notifyItemChanged(data);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        colorPrimary = ResourceUtil.getColorAttributes(getContext(), com.google.android.material.R.attr.colorPrimary);
        textColor = ResourceUtil.getColorAttributes(getContext(), com.google.android.material.R.attr.colorOnSurface);
    }
}