package com.ixuea.superui.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

/**
 * 剪贴板工具类
 */
public class SuperClipboardUtil {
    /**
     * 拷贝文本
     *
     * @param context
     * @param data
     */
    public static void copyText(Context context, String data) {
        //获取剪贴板管理器
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

        //创建一个clipData对象
        ClipData clipData = ClipData.newPlainText("text", data);

        //设置内容到剪贴板
        clipboardManager.setPrimaryClip(clipData);
    }
}
