package com.ixuea.courses.mymusic.component.feed.adapter

import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.model.Resource
import com.ixuea.courses.mymusic.util.ImageUtil
import com.luck.picture.lib.entity.LocalMedia

/**
 * 图片适配器
 */
class ImageAdapter(layoutResId: Int) : BaseQuickAdapter<Any, BaseViewHolder>(layoutResId) {
    override fun convert(holder: BaseViewHolder, item: Any) {
        val iconView = holder.getView<ImageView>(R.id.icon)
        holder.setGone(R.id.close, true)

        when (item) {
            is Resource -> ImageUtil.show(context, iconView, item.uri)
            is LocalMedia -> {
                ImageUtil.showLocalImage(context, iconView, item.compressPath)
                holder.setGone(R.id.close, false)
            }

            is Int -> iconView.setImageResource(item)
        }
    }
}
