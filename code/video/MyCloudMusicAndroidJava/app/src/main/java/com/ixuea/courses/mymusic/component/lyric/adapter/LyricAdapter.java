package com.ixuea.courses.mymusic.component.lyric.adapter;

import android.view.View;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.component.lyric.model.Line;
import com.ixuea.courses.mymusic.component.lyric.view.LyricLineView;

/**
 * 播放界面-歌词列表适配器
 */
public class LyricAdapter extends BaseQuickAdapter<Object, BaseViewHolder> {
    private int selectedIndex;
    private boolean accurate;

    public LyricAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, Object d) {
//        if (d instanceof Line) {
//            //真实数据
//            Line data = (Line) d;
//            holder.setText(R.id.content, data.getData());
//        } else {
//            //不显示内容
//            holder.setText(R.id.content, "");
//        }
//
//        //处理选中状态
//        if (selectedIndex == holder.getAdapterPosition()) {
//            //选中
//            holder.setTextColor(R.id.content, getContext().getColor(R.color.primary));
//        } else {
//            //未选中
//            holder.setTextColor(R.id.content, getContext().getColor(R.color.white));
//        }

        //使用自定义View实现
        LyricLineView contentView = holder.getView(R.id.content);

        if (d instanceof String) {
            contentView.setVisibility(View.GONE);
            contentView.setData(null);
            contentView.setAccurate(false);
        } else {
            contentView.setVisibility(View.VISIBLE);
            contentView.setData((Line) d);
            contentView.setAccurate(accurate);
        }

        //处理选中状态
        if (selectedIndex == holder.getAdapterPosition()) {
            contentView.setLineSelected(true);
        } else {
            contentView.setLineSelected(false);
        }
    }

    /**
     * 设置选中索引
     *
     * @param selectedIndex
     */
    public void setSelectedIndex(int selectedIndex) {
        notifyItemChanged(this.selectedIndex);// 旧行取消高亮

        this.selectedIndex = selectedIndex;

        notifyItemChanged(this.selectedIndex);// 新行设置高亮
    }

    public void setAccurate(boolean accurate) {
        this.accurate = accurate;
    }
}
