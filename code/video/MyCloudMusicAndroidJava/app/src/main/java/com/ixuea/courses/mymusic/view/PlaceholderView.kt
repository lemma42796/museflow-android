package com.ixuea.courses.mymusic.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import com.ixuea.courses.mymusic.databinding.ViewPlaceholderBinding

/**
 * 界面占位控件，可以实现例如：出错了等，点击重新加载。
 */
class PlaceholderView : LinearLayout {
    private lateinit var binding: ViewPlaceholderBinding

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int,
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    private fun init() {
        binding = ViewPlaceholderBinding.inflate(LayoutInflater.from(context))
        addView(
            binding.root,
            LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }

    /**
     * 显示提示
     */
    fun show(titleResource: Int, iconResource: Int) {
        if (titleResource != -1) {
            binding.title.setText(titleResource)
        }

        if (iconResource != -1) {
            binding.icon.setImageResource(iconResource)
        }
    }

    /**
     * 显示提示
     */
    fun show(title: String?, iconResource: Int) {
        title?.let(binding.title::setText)

        if (iconResource != -1) {
            binding.icon.setImageResource(iconResource)
        }
    }

    /**
     * 显示提示
     */
    fun showTitle(data: String?) {
        binding.title.text = data
    }

    /**
     * 显示提示
     */
    fun showTitle(data: Int) {
        binding.title.setText(data)
    }

    /**
     * 显示图标
     */
    fun showIcon(iconResource: Int) {
        binding.icon.setImageResource(iconResource)
    }
}
