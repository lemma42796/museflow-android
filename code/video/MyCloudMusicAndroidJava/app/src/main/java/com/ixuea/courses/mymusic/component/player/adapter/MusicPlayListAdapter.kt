package com.ixuea.courses.mymusic.component.player.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.component.song.model.Song
import com.ixuea.courses.mymusic.manager.MusicListManager

/**
 * 播放列表adapter
 */
class MusicPlayListAdapter(
    layoutResId: Int,
    private val musicListManager: MusicListManager
) : BaseQuickAdapter<Song, BaseViewHolder>(layoutResId) {

    override fun convert(holder: BaseViewHolder, item: Song) {
        val title = buildTitle(item)
        holder.setText(R.id.title, title)

        val colorRes = if (item.id == musicListManager.data?.id) {
            R.color.primary
        } else {
            R.color.black32
        }
        holder.setTextColor(R.id.title, context.getColor(colorRes))
    }

    private fun buildTitle(item: Song): String {
        val title = item.title.orEmpty()
        val singer = item.singer?.nickname.orEmpty()
        return if (singer.isBlank()) title else "$title - $singer"
    }
}
