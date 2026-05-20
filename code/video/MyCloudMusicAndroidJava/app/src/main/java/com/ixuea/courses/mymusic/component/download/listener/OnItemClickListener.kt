package com.ixuea.courses.mymusic.component.download.listener

import com.ixuea.courses.mymusic.adapter.BaseRecyclerViewAdapter

/**
 * Adapter的item点击事件监听器
 */
fun interface OnItemClickListener {
    fun onItemClick(holder: BaseRecyclerViewAdapter.ViewHolder<*>, position: Int)
}
