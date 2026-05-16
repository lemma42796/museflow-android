package com.ixuea.superui.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

public class SuperViewUtil {
    /**
     * 显示
     *
     * @param data
     */
    public static void show(View data) {
        data.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏
     *
     * @param data
     */
    public static void gone(View data) {
        data.setVisibility(View.GONE);
    }

    /**
     * 设置隐藏控件
     *
     * @param view
     * @param gone
     */
    public static void gone(View view, boolean gone) {
        view.setVisibility(gone ? View.GONE : View.VISIBLE);
    }

    /**
     * 显示控件
     *
     * @param view
     * @param show
     */
    public static void show(View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    /**
     * 从父布局移除
     *
     * @param data
     */
    public static void removeFromParent(View data) {
        ((ViewGroup) data.getParent()).removeView(data);
    }

    /**
     * 从view创建bitmap(截图)
     *
     * @param data
     * @return
     */
    public static Bitmap captureBitmap(View data) {
        //创建一个和view一样大小的bitmap
        Bitmap bitmap =
                Bitmap.createBitmap(data.getWidth(), data.getHeight(), Bitmap.Config.ARGB_8888);

        //创建一个画板
        //只是这个画板最终画的内容
        //在Bitmap上
        Canvas canvas = new Canvas(bitmap);

        //获取view的背景
        if (data.getBackground() != null) {
            //如果有背景

            //就显示绘制背景
            data.getBackground().draw(canvas);
        } else {
            //没有背景

            //绘制白色
            canvas.drawColor(Color.WHITE);
        }

        //绘制view内容
        data.draw(canvas);

        //返回bitmap
        return bitmap;
    }

    /**
     * 重新设置宽高
     */
    public static void resize(View view, int width, int height) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
    }
}
