package com.ixuea.courses.mymusic.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

/**
 * 通用PagingDataAdapter
 * 主要实现了一些通用方法
 */
abstract class BasePagingDataAdapter<D : Any, VH : BasePagingDataAdapter.ViewHolder<*>>(
    diffCallback: DiffUtil.ItemCallback<D>,
    protected val context: Context,
) : PagingDataAdapter<D, VH>(diffCallback) {
    val inflater: LayoutInflater = LayoutInflater.from(context)

    /**
     * 通用ViewHolder
     * 主要是添加实现一些公共的逻辑
     */
    abstract class ViewHolder<D>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        /**
         * 绑定数据
         */
        open fun bind(data: D) = Unit

        fun getContext(): Context {
            return itemView.context
        }
    }
}
