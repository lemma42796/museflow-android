package com.ixuea.courses.mymusic.component.discovery.adapter

import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.component.sheet.model.Sheet
import com.ixuea.courses.mymusic.util.ImageUtil

/**
 * 发现界面歌单适配器
 */
class SheetAdapter(layoutResId: Int) : BaseQuickAdapter<Sheet, BaseViewHolder>(layoutResId) {
    override fun convert(holder: BaseViewHolder, item: Sheet) {
        ImageUtil.show(context, holder.getView<ImageView>(R.id.icon), item.icon)
        holder.setText(R.id.title, item.title)
        holder.setText(R.id.more, item.clicksCount.toString())
    }
}
