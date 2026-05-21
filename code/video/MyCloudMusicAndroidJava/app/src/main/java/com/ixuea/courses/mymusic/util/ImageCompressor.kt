package com.ixuea.courses.mymusic.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

object ImageCompressor {
    private val mainHandler = Handler(Looper.getMainLooper())
    private val executorService = Executors.newFixedThreadPool(4)
    private val fileCounter = AtomicInteger()

    interface CompressionCallback {
        fun onCompressionComplete(originalFilePath: String, compressedFilePath: String)

        fun onCompressionError(e: Exception)
    }

    @JvmStatic
    fun compressImagesAsync(context: Context, imageUris: List<Uri>, callback: CompressionCallback) {
        imageUris.forEach { imageUri ->
            compressImageAsync(context, imageUri, callback)
        }
    }

    @JvmStatic
    fun compressImageAsync(context: Context, imageUri: Uri, callback: CompressionCallback) {
        executorService.submit {
            try {
                val originalFilePath = imageUri.toString()
                val compressedFilePath = compressImage(context, imageUri)
                mainHandler.post {
                    callback.onCompressionComplete(originalFilePath, compressedFilePath)
                }
            } catch (e: Exception) {
                mainHandler.post {
                    callback.onCompressionError(e)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun compressImage(context: Context, imageUri: Uri): String {
        val maxSize = 1080
        val contentResolver = context.contentResolver
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }

        contentResolver.openInputStream(imageUri).use { inputStream ->
            BitmapFactory.decodeStream(inputStream, null, options)
        }

        val originalWidth = options.outWidth
        val originalHeight = options.outHeight
        if (originalWidth <= 0 || originalHeight <= 0) {
            throw IOException("Unable to decode image bounds: $imageUri")
        }

        var newWidth = originalWidth
        var newHeight = originalHeight
        if (originalWidth > originalHeight) {
            if (originalWidth > maxSize) {
                newWidth = maxSize
                newHeight = originalHeight * maxSize / originalWidth
            }
        } else if (originalHeight > maxSize) {
            newHeight = maxSize
            newWidth = originalWidth * maxSize / originalHeight
        }

        options.inJustDecodeBounds = false
        options.inSampleSize = calculateInSampleSize(options, newWidth, newHeight)

        val compressedBitmap = contentResolver.openInputStream(imageUri).use { inputStream ->
            BitmapFactory.decodeStream(inputStream, null, options)
        } ?: throw IOException("Unable to decode image: $imageUri")

        val scaledBitmap = Bitmap.createScaledBitmap(compressedBitmap, newWidth, newHeight, true)
        val fileExtension = getFileExtension(context, imageUri)
        val extension = if (fileExtension.equals("png", ignoreCase = true)) "png" else "jpg"
        val uniqueFileName = "compressed_${System.currentTimeMillis()}_${fileCounter.incrementAndGet()}.$extension"

        val cacheDir = context.externalCacheDir ?: context.cacheDir
        val subDir = File(cacheDir, "compressed_images")
        if (!subDir.exists()) {
            subDir.mkdirs()
        }

        val compressedFile = File(subDir, uniqueFileName)
        FileOutputStream(compressedFile).use { out ->
            if (extension == "png") {
                scaledBitmap.compress(Bitmap.CompressFormat.PNG, 6, out)
            } else {
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
            }
        }

        if (scaledBitmap != compressedBitmap) {
            scaledBitmap.recycle()
        }
        compressedBitmap.recycle()

        return compressedFile.absolutePath
    }

    private fun getFileExtension(context: Context, uri: Uri): String {
        val projection = arrayOf(MediaStore.Images.Media.MIME_TYPE)
        context.contentResolver.query(uri, projection, null, null, null).use { cursor ->
            if (cursor != null && cursor.moveToFirst()) {
                val mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE))
                return mimeType.substring(mimeType.lastIndexOf("/") + 1)
            }
        }
        return ""
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }
}
