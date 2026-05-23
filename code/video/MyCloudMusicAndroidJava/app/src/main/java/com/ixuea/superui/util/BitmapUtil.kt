package com.ixuea.superui.util

import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream

/**
 * Bitmap 相关工具方法。
 */
object BitmapUtil {
    @JvmStatic
    fun saveToFile(bitmap: Bitmap, file: File): Boolean {
        return try {
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
