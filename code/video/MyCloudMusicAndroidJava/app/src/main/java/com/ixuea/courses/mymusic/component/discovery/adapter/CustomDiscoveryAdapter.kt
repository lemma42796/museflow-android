package com.ixuea.courses.mymusic.component.discovery.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import com.ixuea.courses.mymusic.adapter.BaseRecyclerViewAdapter
import com.ixuea.courses.mymusic.component.discovery.model.ui.CustomDiscoveryItem
import com.ixuea.courses.mymusic.databinding.ItemCustomDiscoveryBinding

/**
 * 自定义发现界面适配器
 */
class CustomDiscoveryAdapter(
    context: Context,
    private val touchHelper: ItemTouchHelper,
) : BaseRecyclerViewAdapter<CustomDiscoveryItem, CustomDiscoveryAdapter.ViewHolder>(context) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemCustomDiscoveryBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.bind(getData(position))
    }

    inner class ViewHolder(
        private val binding: ItemCustomDiscoveryBinding,
    ) : BaseRecyclerViewAdapter.ViewHolder<CustomDiscoveryItem>(binding.root) {

        init {
            binding.more.setOnTouchListener { _, _ ->
                touchHelper.startDrag(this)
                true
            }
        }

        override fun bind(data: CustomDiscoveryItem) {
            binding.title.setText(data.title)
        }
    }
}
