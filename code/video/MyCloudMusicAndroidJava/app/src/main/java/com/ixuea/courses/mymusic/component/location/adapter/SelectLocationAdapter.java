package com.ixuea.courses.mymusic.component.location.adapter;

import androidx.annotation.NonNull;

import com.amap.api.services.core.PoiItem;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.ixuea.courses.mymusic.R;
import com.ixuea.superui.util.SuperViewUtil;

/**
 * 选择位置适配器
 */
public class SelectLocationAdapter extends BaseQuickAdapter<Object, BaseViewHolder> {
    private int selectedIndex = 0;

    public SelectLocationAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, Object d) {
        if (d instanceof Integer) {
            //不显示位置
            holder.setText(R.id.title, (Integer) d);
            holder.setGone(R.id.address, true);
        } else {
            //com.amap.api.services.core
            PoiItem data = (PoiItem) d;
            holder.setText(R.id.title, data.getTitle());
            holder.setText(R.id.address, getContext().getString(R.string.select_location_address, data.getDistance(), data.getSnippet()));
            holder.setGone(R.id.address, false);
        }

        SuperViewUtil.show(holder.getView(R.id.select), selectedIndex == holder.getLayoutPosition());
    }

    /**
     * 获取选择的数据
     */
    public Object getSelectedData() {
        return getItem(selectedIndex);
    }

    /**
     * 设置点击
     */
    public void setSelected(int position) {
        if (position == selectedIndex) {
            return;
        }

        notifyItemChanged(selectedIndex);
        selectedIndex = position;
        notifyItemChanged(selectedIndex);
    }
}
