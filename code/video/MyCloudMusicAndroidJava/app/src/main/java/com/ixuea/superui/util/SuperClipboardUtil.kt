package com.ixuea.superui.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

/**
 * 剪贴板工具类。
 */
object SuperClipboardUtil {
    @JvmStatic
    fun copyText(context: Context, data: String) {
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("text", data)
        clipboardManager.setPrimaryClip(clipData)
    }
}
