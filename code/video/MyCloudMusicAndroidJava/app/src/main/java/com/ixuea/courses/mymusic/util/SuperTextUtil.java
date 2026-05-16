package com.ixuea.courses.mymusic.util;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ixuea.superui.text.SuperClickableSpan;

/**
 * 文本相关工具类
 */
public class SuperTextUtil {
    /**
     * 设置文本点击
     *
     * @param data
     * @param listener
     * @return
     */
    public static SpannableStringBuilder setHtmlLinkClick(Spanned data, OnLinkClickListener listener) {
        SpannableStringBuilder sb = new SpannableStringBuilder(data);
        URLSpan[] spans = sb.getSpans(0, sb.length(), URLSpan.class);

        for (URLSpan span : spans) {
            int start = sb.getSpanStart(span);
            int end = sb.getSpanEnd(span);
            int flags = sb.getSpanFlags(span);

            sb.setSpan(new SuperClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    listener.onLinkClick(span.getURL());
                }
            }, start, end, flags);
        }

        return sb;
    }

    /**
     * 设置富文本，超链接颜色
     *
     * @param view
     * @param color
     */
    public static void setLinkColor(TextView view, int color) {
        //设置后才可以点击
        view.setMovementMethod(LinkMovementMethod.getInstance());

        //链接的颜色
        view.setLinkTextColor(color);
    }

    /**
     * 链接点击监听器
     */
    public interface OnLinkClickListener {
        void onLinkClick(String data);
    }
}
