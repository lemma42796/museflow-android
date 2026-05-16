package com.ixuea.courses.mymusic.component.discovery.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.component.sheet.model.Sheet;
import com.ixuea.courses.mymusic.util.ImageUtil;

/**
 * 发现界面歌单适配器
 */
public class SheetAdapter extends BaseQuickAdapter<Sheet, BaseViewHolder> {
    public SheetAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, Sheet data) {
        ImageUtil.show(getContext(), holder.getView(R.id.icon), data.getIcon());

        holder.setText(R.id.title, data.getTitle());

        holder.setText(R.id.more, String.valueOf(data.getClicksCount()));
    }
}
