package com.ixuea.courses.mymusic.component.lyric.adapter

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.component.lyric.model.Line
import com.ixuea.courses.mymusic.component.lyric.view.LyricLineView

/**
 * 播放界面-歌词列表适配器
 */
class LyricAdapter : BaseQuickAdapter<Any, BaseViewHolder>(0) {
    private var selectedIndex = 0
    private var accurate = false

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(createItemView(parent))
    }

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

    private fun createItemView(parent: ViewGroup): View {
        val context = parent.context
        val contentView = LyricLineView(context).apply {
            id = R.id.content
            setLyricTextColor(Color.argb(210, 255, 255, 255))
            setLyricSelectedTextColor(Color.rgb(50, 244, 218))
            setLyricSelectedBackgroundColor(Color.argb(42, 255, 255, 255))
            setLyricTextSize(context.resources.getDimensionPixelSize(R.dimen.text_large))
        }
        return LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                context.resources.getDimensionPixelSize(R.dimen.d54),
            )
            addView(
                contentView,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                ),
            )
        }
    }
}
