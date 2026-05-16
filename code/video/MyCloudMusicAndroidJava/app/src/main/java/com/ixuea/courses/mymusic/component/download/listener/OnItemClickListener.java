package com.ixuea.courses.mymusic.component.download.listener;

import com.ixuea.courses.mymusic.adapter.BaseRecyclerViewAdapter;

/**
 * Adapter的item点击事件监听器
 */
public interface OnItemClickListener {
    /**
     * item点击事件
     *
     * @param holder   点击的ViewHolder
     * @param position 点击的位置
     */
    void onItemClick(BaseRecyclerViewAdapter.ViewHolder holder, int position);
}
