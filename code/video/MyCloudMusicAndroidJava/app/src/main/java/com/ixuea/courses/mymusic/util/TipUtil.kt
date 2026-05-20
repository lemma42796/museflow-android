package com.ixuea.courses.mymusic.util

import com.ixuea.courses.mymusic.view.PlaceholderView
import com.ixuea.superui.toast.SuperToast
import com.ixuea.superui.util.SuperViewUtil

/**
 * 提示工具类
 *
 * 主要是判断是否有placeholder，如果有就使用该控件显示提示
 * 如果没有就使用toast提示
 */
object TipUtil {
    /**
     * 显示错误提示
     */
    @JvmStatic
    fun showError(
        toastResource: Int,
        placeholderView: PlaceholderView?,
        placeholderTitle: String?,
    ) {
        showError(toastResource, placeholderView, placeholderTitle, -1)
    }

    /**
     * 显示错误提示
     */
    @JvmStatic
    fun showError(
        toastResource: Int,
        placeholderView: PlaceholderView?,
        placeholderTitleResource: Int,
    ) {
        showError(toastResource, placeholderView, placeholderTitleResource, -1)
    }

    /**
     * 显示错误提示
     */
    @JvmStatic
    fun showError(
        toastResource: Int,
        placeholderView: PlaceholderView?,
        placeholderTitle: String?,
        placeholderIconResource: Int,
    ) {
        if (placeholderView == null) {
            SuperToast.error(toastResource)
        } else {
            SuperViewUtil.show(placeholderView)
            placeholderView.show(placeholderTitle, placeholderIconResource)
        }
    }

    /**
     * 显示错误提示
     */
    @JvmStatic
    fun showError(
        toastResource: Int,
        placeholderView: PlaceholderView?,
        placeholderTitleResource: Int,
        placeholderIconResource: Int,
    ) {
        if (placeholderView == null) {
            SuperToast.error(toastResource)
        } else {
            SuperViewUtil.show(placeholderView)
            placeholderView.show(placeholderTitleResource, placeholderIconResource)
        }
    }

    /**
     * 显示错误提示
     */
    @JvmStatic
    fun showError(toast: String?, placeholderView: PlaceholderView?) {
        if (placeholderView == null) {
            SuperToast.error(toast)
        } else {
            SuperViewUtil.show(placeholderView)
            placeholderView.showTitle(toast)
        }
    }

    /**
     * 显示错误提示
     */
    @JvmStatic
    fun showError(toastResource: Int, placeholderView: PlaceholderView?) {
        if (placeholderView == null) {
            SuperToast.error(toastResource)
        } else {
            SuperViewUtil.show(placeholderView)
            placeholderView.showTitle(toastResource)
        }
    }
}
