package com.ixuea.courses.mymusic.component.feed.domain

import android.content.Context
import android.net.Uri
import com.ixuea.courses.mymusic.component.feed.repository.ImageCompressionRepository
import com.ixuea.courses.mymusic.util.ImageCompressor

class CompressFeedImagesUseCase(
    private val repository: ImageCompressionRepository = ImageCompressionRepository.getInstance(),
) {
    operator fun invoke(
        context: Context,
        imageUris: List<Uri>,
        onComplete: (originalFilePath: String, compressedFilePath: String) -> Unit,
        onError: (Exception) -> Unit = {},
    ) {
        repository.compressImages(
            context,
            imageUris,
            object : ImageCompressor.CompressionCallback {
                override fun onCompressionComplete(
                    originalFilePath: String,
                    compressedFilePath: String,
                ) {
                    onComplete(originalFilePath, compressedFilePath)
                }

                override fun onCompressionError(e: Exception) {
                    onError(e)
                }
            }
        )
    }
}
