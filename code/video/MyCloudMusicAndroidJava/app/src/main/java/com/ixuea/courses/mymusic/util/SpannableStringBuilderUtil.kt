package com.ixuea.courses.mymusic.util

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.view.View
import com.ixuea.courses.mymusic.component.user.domain.NotifyUserDetailRequestedUseCase
import com.ixuea.superui.text.SuperClickableSpan

object SpannableStringBuilderUtil {
    private val notifyUserDetailRequested = NotifyUserDetailRequestedUseCase()

    /**
     * 向SpannableStringBuilder扩展用户点击方法
     *
     * @param start 开始位置
     * @param end 结束位置，不包括
     * @param data 消息
     */
    @JvmStatic
    fun setUserClickSpan(builder: SpannableStringBuilder, start: Int, end: Int, data: String?) {
        builder.setSpan(
            object : SuperClickableSpan() {
                override fun onClick(widget: View) {
                    notifyUserDetailRequested(data)
                }
            },
            start,
            end,
            Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )
    }
}
