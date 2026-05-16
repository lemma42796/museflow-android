package com.ixuea.courses.mymusic.component.feed.repository

import android.content.Context
import android.net.Uri
import com.ixuea.courses.mymusic.util.ImageCompressor

/**
 * Main-safe image compression entrypoint for publishing and image-message flows.
 */
class ImageCompressionRepository private constructor() {
    fun compressImages(
        context: Context,
        imageUris: List<Uri>,
        callback: ImageCompressor.CompressionCallback,
    ) {
        ImageCompressor.compressImagesAsync(context, imageUris, callback)
    }

    companion object {
        @Volatile
        private var instance: ImageCompressionRepository? = null

        @JvmStatic
        fun getInstance(): ImageCompressionRepository {
            return instance ?: synchronized(this) {
                instance ?: ImageCompressionRepository().also {
                    instance = it
                }
            }
        }
    }
}
