package com.ixuea.courses.mymusic.util

import android.content.Context

object PackageUtil {
    /**
     * 判断应用是否安装
     */
    @JvmStatic
    fun isInstalled(context: Context, data: String): Boolean {
        try {
            context.packageManager.getPackageInfo(data, 0)
        } catch (x: Exception) {
            return false
        }
        return false
    }
}
