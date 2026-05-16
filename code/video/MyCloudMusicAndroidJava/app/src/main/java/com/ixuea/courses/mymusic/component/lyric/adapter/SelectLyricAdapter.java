package com.ixuea.courses.mymusic.component.lyric.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.component.lyric.model.Line;

import java.util.List;

/**
 * 选择歌词适配器
 */
public class SelectLyricAdapter extends BaseQuickAdapter<Line, BaseViewHolder> {
    /**
     * 1表示选中
     * 0表示没选中
     * 变量取名尽量还是符合英语复数
     * 这样很容易能看出是一个对象
     * 还是多个对象
     */
    private int[] selectedIndexes;

    public SelectLyricAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, Line data) {
        holder.setText(R.id.title, data.getData());

        if (isSelected(holder.getLayoutPosition())) {
            holder.setVisible(R.id.select, true);
            holder.setBackgroundColor(R.id.container, getContext().getColor(R.color.black));
        } else {
            holder.setVisible(R.id.select, false);
            holder.setBackgroundColor(R.id.container, getContext().getColor(R.color.transparent));
        }
    }

    @Override
    public void setNewInstance(@Nullable List<Line> data) {
        super.setNewInstance(data);

        //创建一个和数据长度一样的数组
        selectedIndexes = new int[data.size()];
    }

    /**
     * 当前位置是否选中
     *
     * @param position
     * @return
     */
    public boolean isSelected(int position) {
        return selectedIndexes[position] == 1;
    }

    /**
     * 设置位置是否选中
     *
     * @param position
     * @param isSelected
     */
    public void setSelected(int position, boolean isSelected) {
        selectedIndexes[position] = isSelected ? 1 : 0;
        notifyItemChanged(position);
    }

    /**
     * 获取选中的索引
     *
     * @return
     */
    public int[] getSelectedIndexes() {
        return selectedIndexes;
    }
}
