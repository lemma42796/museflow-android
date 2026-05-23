package com.ixuea.courses.mymusic.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream

/**
 * 存储相关工具栏
 */
object StorageUtil {
    /**
     * mp3
     */
    const val MP3 = "mp3"

    private const val COLUMN_DATA = "_data"

    /**
     * 获取应用扩展sdcard中的路径
     */
    @JvmStatic
    fun getExternalPath(context: Context, userId: String, title: String?, suffix: String): File {
        val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        val path = String.format("MuseFlow/%s/%s/%s.%s", userId, suffix, title, suffix)
        val file = File(dir, path)

        if (!file.parentFile!!.exists()) {
            file.parentFile!!.mkdirs()
        }

        return file
    }

    /**
     * 根据id获取audio content uri
     */
    @JvmStatic
    fun getAudioContentUri(id: Long): String {
        return MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.buildUpon()
            .appendPath(id.toString())
            .build()
            .toString()
    }

    /**
     * 保存图片到系统相册
     */
    @JvmStatic
    fun savePicture(context: Context, data: Bitmap): Uri? {
        val uri = insertPictureMediaStore(
            context,
            String.format("museflow_code_%s.jpg", SuperDateUtil.nowyyyyMMddHHmmss())
        ) ?: return null

        return saveFile(context, data, uri)
    }

    /**
     * 创建相册图片路径
     */
    private fun insertPictureMediaStore(context: Context, name: String): Uri? {
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, name)
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
        }

        return context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
    }

    /**
     * 保存文件到uri对应的路径
     */
    private fun saveFile(context: Context, data: Bitmap, uri: Uri): Uri? {
        try {
            val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "w")

            try {
                FileOutputStream(parcelFileDescriptor!!.fileDescriptor).use { out ->
                    data.compress(Bitmap.CompressFormat.JPEG, 90, out)
                    return uri
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 获取MediaStore uri的路径
     */
    @JvmStatic
    fun getMediaStorePath(context: Context, uri: Uri): String? {
        return getDataColumn(context, uri)
    }

    /**
     * 获取uri对应的data列值
     * 其实就是文件路径
     * 这种写法支持MediaStore，ContentProviders
     */
    private fun getDataColumn(context: Context, uri: Uri): String? {
        try {
            context.contentResolver.query(
                uri,
                arrayOf(COLUMN_DATA),
                null,
                null,
                null
            ).use { cursor ->
                if (cursor != null && cursor.moveToFirst()) {
                    val index = cursor.getColumnIndexOrThrow(COLUMN_DATA)
                    return cursor.getString(index)
                }
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }

        return null
    }
}
