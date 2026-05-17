package com.ixuea.courses.mymusic.component.player.adapter

import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.component.song.model.Song

/**
 * 简单播放界面列表适配器
 */
class SimplePlayerAdapter(layoutResId: Int) : BaseQuickAdapter<Song, BaseViewHolder>(layoutResId) {
    private var selectedIndex = -1

    /**
     * 显示数据
     */
    override fun convert(holder: BaseViewHolder, item: Song) {
        val titleView = holder.getView<TextView>(android.R.id.text1)

        titleView.text = item.title

        val colorRes = if (selectedIndex == holder.bindingAdapterPosition) {
            R.color.primary
        } else {
            R.color.black32
        }
        titleView.setTextColor(context.getColor(colorRes))
    }

    /**
     * 选中音乐
     */
    fun setSelectedIndex(selectedIndex: Int) {
        if (this.selectedIndex != -1) {
            notifyItemChanged(this.selectedIndex)
        }

        this.selectedIndex = selectedIndex

        if (this.selectedIndex != -1) {
            notifyItemChanged(this.selectedIndex)
        }
    }
}
