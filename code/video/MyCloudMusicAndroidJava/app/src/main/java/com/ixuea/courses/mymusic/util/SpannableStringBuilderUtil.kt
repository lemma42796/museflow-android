package com.ixuea.courses.mymusic.util

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.view.View
import com.ixuea.courses.mymusic.component.user.model.event.UserDetailEvent
import com.ixuea.superui.text.SuperClickableSpan
import org.greenrobot.eventbus.EventBus

object SpannableStringBuilderUtil {
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
                    EventBus.getDefault().post(UserDetailEvent(data))
                }
            },
            start,
            end,
            Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )
    }
}
