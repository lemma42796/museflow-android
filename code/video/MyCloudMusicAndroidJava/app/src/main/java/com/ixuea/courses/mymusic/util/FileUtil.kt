package com.ixuea.courses.mymusic.util

import android.content.Context
import android.os.Environment
import java.io.File

/**
 * 文件工具类
 */
object FileUtil {
    /**
     * 获取缓存广告目录
     */
    @JvmStatic
    fun adFile(context: Context, data: String): File {
        val file = File(
            context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)!!.absolutePath + "/ads",
            data
        )
        val parentFile = file.parentFile!!
        if (!parentFile.exists()) {
            parentFile.mkdirs()
        }

        return file
    }

    /**
     * 文件大小格式化
     *
     * @param value 文件大小；单位byte
     * @return 格式化的文件大小；例如：1.22M
     */
    @JvmStatic
    fun formatFileSize(value: Long): String {
        if (value > 0) {
            val size = value.toDouble()

            val kiloByte = size / 1024
            if (kiloByte < 1 && kiloByte > 0) {
                return String.format("%.2fByte", size)
            }

            val megaByte = kiloByte / 1024
            if (megaByte < 1) {
                return String.format("%.2fK", kiloByte)
            }

            val gigaByte = megaByte / 1024
            if (gigaByte < 1) {
                return String.format("%.2fM", megaByte)
            }

            val teraByte = gigaByte / 1024
            if (teraByte < 1) {
                return String.format("%.2fG", gigaByte)
            }

            return String.format("%.2fT", teraByte)
        }
        return "0K"
    }
}
