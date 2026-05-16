package com.ixuea.courses.mymusic.util;

import android.graphics.Paint;

public class TextUtil {
    /**
     * 获取文本的宽度
     *
     * @param paint
     * @param data
     * @return
     */
    public static float getTextWidth(Paint paint, String data) {
        return paint.measureText(data);
    }

    /**
     * 获取文本的高度
     *
     * @param paint
     * @return
     */
    public static float getTextHeight(Paint paint) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return (float) Math.ceil(fontMetrics.descent - fontMetrics.ascent);
    }
}
