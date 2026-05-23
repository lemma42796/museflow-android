package com.ixuea.superui.text

import android.text.TextPaint
import android.text.style.ClickableSpan

/**
 * 自定义 ClickableSpan，去除下划线。
 */
abstract class SuperClickableSpan : ClickableSpan() {
    override fun updateDrawState(ds: TextPaint) {
        ds.color = ds.linkColor
        ds.isUnderlineText = false
    }
}
