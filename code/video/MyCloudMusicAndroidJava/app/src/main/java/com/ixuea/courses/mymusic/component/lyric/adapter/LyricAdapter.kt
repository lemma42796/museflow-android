package com.ixuea.courses.mymusic.component.lyric.adapter

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.component.lyric.model.Line
import com.ixuea.courses.mymusic.component.lyric.view.LyricLineView

/**
 * 播放界面-歌词列表适配器
 */
class LyricAdapter(layoutResId: Int) : BaseQuickAdapter<Any, BaseViewHolder>(layoutResId) {
    private var selectedIndex = 0
    private var accurate = false

    override fun convert(holder: BaseViewHolder, item: Any) {
        val contentView = holder.getView<LyricLineView>(R.id.content)

        if (item is String) {
            contentView.visibility = View.GONE
            contentView.setData(null)
            contentView.setAccurate(false)
        } else {
            contentView.visibility = View.VISIBLE
            contentView.setData(item as Line)
            contentView.setAccurate(accurate)
        }

        contentView.setLineSelected(selectedIndex == holder.bindingAdapterPosition)
    }

    /**
     * 设置选中索引
     */
    fun setSelectedIndex(selectedIndex: Int) {
        if (this.selectedIndex in 0 until itemCount) {
            notifyItemChanged(this.selectedIndex)
        }

        this.selectedIndex = selectedIndex

        if (this.selectedIndex in 0 until itemCount) {
            notifyItemChanged(this.selectedIndex)
        }
    }

    fun setAccurate(accurate: Boolean) {
        this.accurate = accurate
    }
}
