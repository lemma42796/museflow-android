package com.ixuea.courses.mymusic.util;

import android.animation.ValueAnimator;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.animation.AccelerateInterpolator;

/**
 * 使用动画方式切换Drawable工具类
 */
public class SwitchDrawableUtil {
    /**
     * 背景（原来的图片）索引
     */
    private static final int INDEX_BACKGROUND = 0;

    /**
     * 前景（新图片）索引
     */
    private static final int INDEX_FOREGROUND = 1;
    /**
     * 动画执行时间
     * 单位：毫秒
     */
    private static final int DURATION_ANIMATION = 300;
    /**
     * 多层drawable
     */
    private final LayerDrawable layerDrawable;
    private ValueAnimator animator;

    /**
     * 构造方法
     *
     * @param backgroundDrawable 背景（原来的图片）
     * @param foregroundDrawable 前景（新图片）
     */
    public SwitchDrawableUtil(Drawable backgroundDrawable, Drawable foregroundDrawable) {
        //创建数据
        Drawable[] drawables = new Drawable[2];

        if (backgroundDrawable == null) {
            //添加drawable
            drawables[INDEX_BACKGROUND] = foregroundDrawable;
        } else {
            //添加drawable
            drawables[INDEX_BACKGROUND] = backgroundDrawable;
        }

        drawables[INDEX_FOREGROUND] = foregroundDrawable;

        //创建多层drawable
        layerDrawable = new LayerDrawable(drawables);

        //初始化动画
        initAnimation();
    }

    /**
     * 初始化动画
     */
    private void initAnimation() {
        //属性动画
        //从0变为255
        animator = ValueAnimator.ofFloat(0f, 255.0f);

        //设置执行时间
        animator.setDuration(DURATION_ANIMATION);

        //插值器
        //加速插值器
        animator.setInterpolator(new AccelerateInterpolator());

        //添加监听器
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            /**
             * 每次回调方法
             * @param animation
             */
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //获取动画值
                float alpha = (float) animation.getAnimatedValue();

                //前景慢慢变的不透明
                layerDrawable.getDrawable(INDEX_FOREGROUND).setAlpha((int) alpha);
            }
        });
    }

    /**
     * 获取多层drawable
     *
     * @return
     */
    public LayerDrawable getDrawable() {
        return layerDrawable;
    }

    /**
     * 开始执行动画
     */
    public void start() {
        animator.start();
    }
}
