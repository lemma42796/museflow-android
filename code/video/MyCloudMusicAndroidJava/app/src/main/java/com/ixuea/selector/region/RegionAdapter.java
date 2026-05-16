package com.ixuea.selector.region;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.ixuea.courses.mymusic.R;

import java.util.List;

public class RegionAdapter extends BaseQuickAdapter<Region, BaseViewHolder> {

    public RegionAdapter(List<Region> data) {
        super(R.layout.item_list_region, data);
    }

    @Override
    protected void convert(BaseViewHolder holder, Region regionModel) {
        holder.setText(R.id.name, regionModel.getName());
    }
}
