package com.ixuea.courses.mymusic.util;

import com.ixuea.courses.mymusic.view.PlaceholderView;
import com.ixuea.superui.toast.SuperToast;
import com.ixuea.superui.util.SuperViewUtil;

/**
 * 提示工具类
 * <p>
 * 主要是判断是否有placeholder，如果有就使用该控件显示提示
 * 如果没有就使用toast提示
 */
public class TipUtil {
    /**
     * 显示错误提示
     *
     * @param toastResource
     */
    public static void showError(int toastResource,
                                 PlaceholderView placeholderView,
                                 String placeholderTitle) {
        showError(toastResource, placeholderView, placeholderTitle, -1);
    }

    /**
     * 显示错误提示
     *
     * @param toastResource
     */
    public static void showError(int toastResource,
                                 PlaceholderView placeholderView,
                                 int placeholderTitleResource) {
        showError(toastResource, placeholderView, placeholderTitleResource, -1);
    }

    /**
     * 显示错误提示
     *
     * @param toastResource
     */
    public static void showError(int toastResource,
                                 PlaceholderView placeholderView,
                                 String placeholderTitle,
                                 int placeholderIconResource) {
        if (placeholderView == null) {
            SuperToast.error(toastResource);
        } else {
            SuperViewUtil.show(placeholderView);
            placeholderView.show(placeholderTitle, placeholderIconResource);
        }
    }

    /**
     * 显示错误提示
     *
     * @param toastResource
     */
    public static void showError(int toastResource,
                                 PlaceholderView placeholderView,
                                 int placeholderTitleResource,
                                 int placeholderIconResource) {
        if (placeholderView == null) {
            SuperToast.error(toastResource);
        } else {
            SuperViewUtil.show(placeholderView);
            placeholderView.show(placeholderTitleResource, placeholderIconResource);
        }
    }

    /**
     * 显示错误提示
     *
     * @param toast
     */
    public static void showError(String toast, PlaceholderView placeholderView) {
        if (placeholderView == null) {
            SuperToast.error(toast);
        } else {
            SuperViewUtil.show(placeholderView);
            placeholderView.showTitle(toast);
        }
    }

    /**
     * 显示错误提示
     */
    public static void showError(int toastResource, PlaceholderView placeholderView) {
        if (placeholderView == null) {
            SuperToast.error(toastResource);
        } else {
            SuperViewUtil.show(placeholderView);
            placeholderView.showTitle(toastResource);
        }
    }
}
