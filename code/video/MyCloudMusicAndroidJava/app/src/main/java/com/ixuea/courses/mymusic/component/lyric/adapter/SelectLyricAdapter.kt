package com.ixuea.courses.mymusic.component.lyric.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.component.lyric.model.Line

/**
 * 选择歌词适配器
 */
class SelectLyricAdapter(layoutResId: Int) : BaseQuickAdapter<Line, BaseViewHolder>(layoutResId) {
    /**
     * 1表示选中，0表示没选中
     */
    private var selectedIndexes: IntArray = IntArray(0)

    override fun convert(holder: BaseViewHolder, item: Line) {
        holder.setText(R.id.title, item.data)

        if (isSelected(holder.bindingAdapterPosition)) {
            holder.setVisible(R.id.select, true)
            holder.setBackgroundColor(R.id.container, context.getColor(R.color.black))
        } else {
            holder.setVisible(R.id.select, false)
            holder.setBackgroundColor(R.id.container, context.getColor(R.color.transparent))
        }
    }

    override fun setNewInstance(list: MutableList<Line>?) {
        super.setNewInstance(list)
        selectedIndexes = IntArray(list?.size ?: 0)
    }

    /**
     * 当前位置是否选中
     */
    fun isSelected(position: Int): Boolean {
        return position in selectedIndexes.indices && selectedIndexes[position] == SELECTED
    }

    /**
     * 设置位置是否选中
     */
    fun setSelected(position: Int, isSelected: Boolean) {
        if (position !in selectedIndexes.indices) {
            return
        }

        selectedIndexes[position] = if (isSelected) SELECTED else UNSELECTED
        notifyItemChanged(position)
    }

    fun getSelectedIndexes(): IntArray {
        return selectedIndexes
    }

    companion object {
        private const val UNSELECTED = 0
        private const val SELECTED = 1
    }
}
