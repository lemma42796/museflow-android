package com.ixuea.courses.mymusic.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DimenRes
import androidx.core.content.ContextCompat
import com.ixuea.courses.mymusic.R

/**
 * 界面占位控件，可以实现例如：出错了等，点击重新加载。
 */
class PlaceholderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {
    private val icon = ImageView(context)
    private val title = TextView(context)

    init {
        orientation = VERTICAL
        gravity = Gravity.CENTER
        setBackgroundColor(resolveColorAttr(android.R.attr.colorBackground))
        setPaddingByDimen(R.dimen.padding_outer)
        addView(
            icon.apply {
                setImageResource(R.drawable.alert)
            },
            LayoutParams(dp(150), dp(150)),
        )
        addView(
            title.apply {
                setText(R.string.network_error)
                setTextColor(ContextCompat.getColor(context, R.color.black80))
                setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.text_small))
            },
            LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                topMargin = dimenPx(R.dimen.padding_large)
            },
        )
    }

    /**
     * 显示提示
     */
    fun show(titleResource: Int, iconResource: Int) {
        if (titleResource != -1) {
            title.setText(titleResource)
        }

        if (iconResource != -1) {
            icon.setImageResource(iconResource)
        }
    }

    /**
     * 显示提示
     */
    fun show(title: String?, iconResource: Int) {
        title?.let(this.title::setText)

        if (iconResource != -1) {
            icon.setImageResource(iconResource)
        }
    }

    /**
     * 显示提示
     */
    fun showTitle(data: String?) {
        title.text = data
    }

    /**
     * 显示提示
     */
    fun showTitle(data: Int) {
        title.setText(data)
    }

    /**
     * 显示图标
     */
    fun showIcon(iconResource: Int) {
        icon.setImageResource(iconResource)
    }

    private fun View.setPaddingByDimen(@DimenRes resId: Int) {
        val padding = dimenPx(resId)
        setPadding(padding, padding, padding, padding)
    }

    private fun dimenPx(@DimenRes resId: Int): Int {
        return resources.getDimensionPixelSize(resId)
    }

    private fun dp(value: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            value.toFloat(),
            resources.displayMetrics,
        ).toInt()
    }

    private fun resolveColorAttr(attr: Int): Int {
        val typedValue = TypedValue()
        val resolved = context.theme.resolveAttribute(attr, typedValue, true)
        if (!resolved) {
            return Color.TRANSPARENT
        }

        return if (typedValue.resourceId != 0) {
            ContextCompat.getColor(context, typedValue.resourceId)
        } else {
            typedValue.data
        }
    }
}
