package com.ixuea.courses.mymusic.util;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.View;

import androidx.annotation.NonNull;

import com.ixuea.courses.mymusic.component.user.model.event.UserDetailEvent;
import com.ixuea.superui.text.SuperClickableSpan;

import org.greenrobot.eventbus.EventBus;

public class SpannableStringBuilderUtil {
    /**
     * 向SpannableStringBuilder扩展用户点击方法
     *
     * @param start 开始位置
     * @param end   结束位置，不包括
     * @param data  消息
     */
    public static void setUserClickSpan(SpannableStringBuilder builder, int start, int end, String data) {
        builder.setSpan(new SuperClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                EventBus.getDefault().post(new UserDetailEvent(data));
            }
        }, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
    }
}
