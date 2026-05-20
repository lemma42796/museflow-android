package com.ixuea.courses.mymusic.util

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.view.View
import android.widget.TextView
import com.ixuea.superui.text.SuperClickableSpan

/**
 * 文本相关工具类
 */
object SuperTextUtil {
    /**
     * 设置文本点击
     */
    @JvmStatic
    fun setHtmlLinkClick(
        data: Spanned,
        listener: OnLinkClickListener,
    ): SpannableStringBuilder {
        val builder = SpannableStringBuilder(data)
        val spans = builder.getSpans(0, builder.length, URLSpan::class.java)

        for (span in spans) {
            val start = builder.getSpanStart(span)
            val end = builder.getSpanEnd(span)
            val flags = builder.getSpanFlags(span)

            builder.setSpan(
                object : SuperClickableSpan() {
                    override fun onClick(widget: View) {
                        listener.onLinkClick(span.url)
                    }
                },
                start,
                end,
                flags
            )
        }

        return builder
    }

    /**
     * 设置富文本，超链接颜色
     */
    @JvmStatic
    fun setLinkColor(view: TextView, color: Int) {
        view.movementMethod = LinkMovementMethod.getInstance()
        view.setLinkTextColor(color)
    }

    /**
     * 链接点击监听器
     */
    fun interface OnLinkClickListener {
        fun onLinkClick(data: String)
    }
}
