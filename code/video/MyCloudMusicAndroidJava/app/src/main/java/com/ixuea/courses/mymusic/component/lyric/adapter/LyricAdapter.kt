package com.ixuea.courses.mymusic.component.lyric.adapter

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.AttrRes
import androidx.core.content.ContextCompat
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
            setLyricTextColor(resolveColorAttr(context, R.attr.colorLightWhite))
            setLyricSelectedTextColor(ContextCompat.getColor(context, R.color.primary))
            setLyricTextSize(context.resources.getDimensionPixelSize(R.dimen.text_large))
        }
        return LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                context.resources.getDimensionPixelSize(R.dimen.d40),
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

    private fun resolveColorAttr(context: Context, @AttrRes attr: Int): Int {
        val typedValue = TypedValue()
        val resolved = context.theme.resolveAttribute(attr, typedValue, true)
        if (!resolved) {
            return Color.WHITE
        }
        return if (typedValue.resourceId != 0) {
            ContextCompat.getColor(context, typedValue.resourceId)
        } else {
            typedValue.data
        }
    }
}
