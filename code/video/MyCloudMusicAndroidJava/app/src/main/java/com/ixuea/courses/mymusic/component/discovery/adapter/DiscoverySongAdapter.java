package com.ixuea.courses.mymusic.component.discovery.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.component.song.model.Song;
import com.ixuea.courses.mymusic.util.ImageUtil;

/**
 * 发现界面单曲适配器
 */
public class DiscoverySongAdapter extends BaseQuickAdapter<Song, BaseViewHolder> {
    public DiscoverySongAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, Song data) {
        ImageUtil.show(getContext(), holder.getView(R.id.icon), data.getIcon());
        holder.setText(R.id.title, data.getTitle());
        holder.setText(R.id.more, String.format("%s-%s", data.getSinger().getNickname(), "专辑名称"));

        holder.setGone(R.id.divider_small, holder.getLayoutPosition() == getItemCount() - 1);
    }
}
