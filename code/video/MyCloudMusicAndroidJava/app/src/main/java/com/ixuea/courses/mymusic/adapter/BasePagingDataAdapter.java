package com.ixuea.courses.mymusic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 通用PagingDataAdapter
 * 主要实现了一些通用方法
 */
public abstract class BasePagingDataAdapter<D, VH extends BasePagingDataAdapter.ViewHolder> extends PagingDataAdapter<D, VH> {

    protected final Context context;

    private final LayoutInflater inflater;

    public BasePagingDataAdapter(@NonNull DiffUtil.ItemCallback<D> diffCallback, Context context) {
        super(diffCallback);
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public LayoutInflater getInflater() {
        return inflater;
    }

    /**
     * 通用ViewHolder
     * 主要是添加实现一些公共的逻辑
     */
    public abstract static class ViewHolder<D> extends RecyclerView.ViewHolder {

        /**
         * 构造方法
         *
         * @param itemView
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        /**
         * 绑定数据
         *
         * @param data
         */
        public void bind(D data) {

        }

        public Context getContext() {
            return itemView.getContext();
        }
    }
}
