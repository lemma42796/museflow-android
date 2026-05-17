package com.ixuea.courses.mymusic.component.discovery.adapter

import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.component.song.model.Song
import com.ixuea.courses.mymusic.util.ImageUtil

/**
 * 发现界面单曲适配器
 */
class DiscoverySongAdapter(layoutResId: Int) : BaseQuickAdapter<Song, BaseViewHolder>(layoutResId) {
    override fun convert(holder: BaseViewHolder, item: Song) {
        ImageUtil.show(context, holder.getView<ImageView>(R.id.icon), item.icon)
        holder.setText(R.id.title, item.title)
        holder.setText(R.id.more, "%s-%s".format(item.singer.nickname, "专辑名称"))
        holder.setGone(R.id.divider_small, holder.layoutPosition == itemCount - 1)
    }
}
