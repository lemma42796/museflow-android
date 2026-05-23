package com.ixuea.superui.toast

import android.content.Context
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.ixuea.courses.mymusic.R

/**
 * 超级Toast
 *
 * 显示效果类似QQ，在顶部显示，宽度和屏幕宽度差不多。
 */
object SuperToast {
    private lateinit var context: Context

    @JvmStatic
    fun init(context: Context) {
        this.context = context.applicationContext
    }

    @JvmStatic
    fun show(content: String) {
        show(content, R.drawable.shape_toast_background, Toast.LENGTH_LONG)
    }

    @JvmStatic
    fun show(@StringRes content: Int) {
        show(context.getString(content), R.drawable.shape_toast_background, Toast.LENGTH_LONG)
    }

    @JvmStatic
    fun error(@StringRes content: Int) {
        show(context.getString(content), R.drawable.shape_toast_error_background, Toast.LENGTH_LONG)
    }

    @JvmStatic
    fun error(content: String?) {
        show(content.orEmpty(), R.drawable.shape_toast_error_background, Toast.LENGTH_LONG)
    }

    @JvmStatic
    fun success(@StringRes content: Int) {
        show(context.getString(content), R.drawable.shape_toast_success_background, Toast.LENGTH_LONG)
    }

    @JvmStatic
    fun success(content: String) {
        show(content, R.drawable.shape_toast_success_background, Toast.LENGTH_LONG)
    }

    @JvmStatic
    fun show(content: String, @DrawableRes background: Int, duration: Int) {
        show(content, background, duration, Gravity.FILL_HORIZONTAL or Gravity.TOP, 0, 130)
    }

    @Suppress("DEPRECATION")
    @JvmStatic
    fun show(
        content: String,
        @DrawableRes background: Int,
        duration: Int,
        gravity: Int,
        xOffset: Int,
        yOffset: Int,
    ) {
        val toast = Toast(context).apply {
            setDuration(duration)
            setGravity(gravity, xOffset, yOffset)
        }

        val root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dimen(R.dimen.d20), 0, dimen(R.dimen.d20), 0)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
        }
        val container = LinearLayout(context).apply {
            setGravity(Gravity.CENTER_HORIZONTAL)
            orientation = LinearLayout.HORIZONTAL
            setBackgroundResource(background)
            setPadding(
                dimen(R.dimen.padding_meddle),
                dimen(R.dimen.padding_outer),
                dimen(R.dimen.padding_meddle),
                dimen(R.dimen.padding_outer),
            )
        }
        val contentView = TextView(context).apply {
            text = content
            setTextColor(resources.getColor(R.color.white, context.theme))
            setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.s16))
            setPadding(dimen(R.dimen.padding_small), dimen(R.dimen.padding_small), dimen(R.dimen.padding_small), dimen(R.dimen.padding_small))
        }

        container.addView(
            contentView,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            ),
        )
        root.addView(
            container,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            ),
        )
        toast.view = root
        toast.show()
    }

    private fun dimen(resId: Int): Int {
        return context.resources.getDimensionPixelSize(resId)
    }
}
