package com.ixuea.courses.mymusic.util

import android.animation.ValueAnimator
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.view.animation.AccelerateInterpolator

/**
 * 使用动画方式切换Drawable工具类
 */
class SwitchDrawableUtil(
    backgroundDrawable: Drawable?,
    foregroundDrawable: Drawable,
) {
    /**
     * 多层drawable
     */
    val drawable: LayerDrawable

    private val animator: ValueAnimator

    init {
        val drawables = arrayOf(backgroundDrawable ?: foregroundDrawable, foregroundDrawable)
        drawable = LayerDrawable(drawables)
        animator = initAnimation()
    }

    /**
     * 初始化动画
     */
    private fun initAnimation(): ValueAnimator {
        val animator = ValueAnimator.ofFloat(0f, 255.0f)
        animator.duration = DURATION_ANIMATION.toLong()
        animator.interpolator = AccelerateInterpolator()
        animator.addUpdateListener { animation ->
            val alpha = animation.animatedValue as Float
            drawable.getDrawable(INDEX_FOREGROUND).alpha = alpha.toInt()
        }
        return animator
    }

    /**
     * 开始执行动画
     */
    fun start() {
        animator.start()
    }

    private companion object {
        /**
         * 背景（原来的图片）索引
         */
        private const val INDEX_BACKGROUND = 0

        /**
         * 前景（新图片）索引
         */
        private const val INDEX_FOREGROUND = 1

        /**
         * 动画执行时间
         * 单位：毫秒
         */
        private const val DURATION_ANIMATION = 300
    }
}
