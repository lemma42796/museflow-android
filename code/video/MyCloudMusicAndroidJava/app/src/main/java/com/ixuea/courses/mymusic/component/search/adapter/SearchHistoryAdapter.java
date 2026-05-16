package com.ixuea.courses.mymusic.component.search.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.component.search.model.SearchHistory;

/**
 * 搜索历史适配器
 */
public class SearchHistoryAdapter extends BaseQuickAdapter<SearchHistory, BaseViewHolder> {
    public SearchHistoryAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, SearchHistory data) {
        holder.setText(R.id.title, data.getContent());
    }
}
