package com.ixuea.courses.mymusic.component.player.adapter;

import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.component.song.model.Song;

/**
 * 简单播放界面列表适配器
 */
public class SimplePlayerAdapter extends BaseQuickAdapter<Song, BaseViewHolder> {
    private int selectedIndex = -1;

    public SimplePlayerAdapter(int layoutResId) {
        super(layoutResId);
    }

    /**
     * 显示数据
     *
     * @param holder
     * @param data
     */
    @Override
    protected void convert(@NonNull BaseViewHolder holder, Song data) {
        //获取到文本控件
        TextView titleView = holder.getView(android.R.id.text1);

        titleView.setText(data.getTitle());

        if (selectedIndex == holder.getAdapterPosition()) {
            titleView.setTextColor(getContext().getColor(R.color.primary));
        } else {
            titleView.setTextColor(getContext().getColor(R.color.black32));
        }
    }

    /**
     * 选中音乐
     *
     * @param selectedIndex
     */
    public void setSelectedIndex(int selectedIndex) {
        if (this.selectedIndex != -1) {
            //先刷新上一行
            notifyItemChanged(this.selectedIndex);
        }

        //保存选中索引
        this.selectedIndex = selectedIndex;

        if (this.selectedIndex != -1) {
            //刷新当前行
            notifyItemChanged(this.selectedIndex);
        }
    }
}
