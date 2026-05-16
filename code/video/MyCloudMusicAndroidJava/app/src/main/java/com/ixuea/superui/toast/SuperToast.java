package com.ixuea.superui.toast;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.ixuea.courses.mymusic.R;

/**
 * 超级Toast
 * <p>
 * 显示效果类似QQ，在顶部显示，宽度和屏幕宽度差不多
 * 显示到顶部的好处是，不会挡住键盘，因为很多时候显示提示的时候，都是用户输入时产生的
 * 如果不想显示到顶部，可以设置gravity，或者用户默认的Toast
 */
public class SuperToast {
    private static Context context;
    private static LayoutInflater inflater;

    public static void init(Context context) {
        SuperToast.context = context;
        inflater = LayoutInflater.from(context);
    }

    /**
     * 显示文本提示
     *
     * @param content
     */
    public static void show(String content) {
        show(content, R.drawable.shape_toast_background, Toast.LENGTH_LONG);
    }

    /**
     * 显示资源文本提示
     *
     * @param content
     */
    public static void show(@StringRes int content) {
        show(context.getString(content), R.drawable.shape_toast_background, Toast.LENGTH_LONG);
    }

    /**
     * 错误提示
     *
     * @param content
     */
    public static void error(@StringRes int content) {
        show(context.getString(content), R.drawable.shape_toast_error_background, Toast.LENGTH_LONG);
    }

    /**
     * 错误提示
     *
     * @param content
     */
    public static void error(String content) {
        show(content, R.drawable.shape_toast_error_background, Toast.LENGTH_LONG);
    }

    /**
     * 成功提示
     *
     * @param content
     */
    public static void success(@StringRes int content) {
        show(context.getString(content), R.drawable.shape_toast_success_background, Toast.LENGTH_LONG);
    }

    /**
     * 成功提示
     *
     * @param content
     */
    public static void success(String content) {
        show(content, R.drawable.shape_toast_success_background, Toast.LENGTH_LONG);
    }

    public static void show(String content, @DrawableRes int background, int duration) {
        show(content, background, duration, Gravity.FILL_HORIZONTAL | Gravity.TOP, 0, 130);
    }

    /**
     * 显示
     *
     * @param content    显示的内容
     * @param background 背景
     * @param duration   时长
     * @param gravity    位置
     * @param xOffset    x轴偏移
     * @param yOffset    y轴偏移
     */
    public static void show(String content, @DrawableRes int background, int duration, int gravity, int xOffset, int yOffset) {
        Toast toast = new Toast(context);

        //设置时长
        toast.setDuration(duration);

        //设置位置
        toast.setGravity(gravity, xOffset, yOffset);

        // 加载布局
        View view = inflater.inflate(R.layout.super_toast, null);
        LinearLayout containerView = view.findViewById(R.id.container);

        //设置背景
        containerView.setBackgroundResource(background);

        TextView contentView = view.findViewById(R.id.content);
        contentView.setText(content);

        //设置自定义布局
        toast.setView(view);

        //显示
        toast.show();
    }
}
