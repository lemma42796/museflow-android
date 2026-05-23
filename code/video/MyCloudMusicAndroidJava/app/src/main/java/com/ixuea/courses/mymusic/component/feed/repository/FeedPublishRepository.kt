package com.ixuea.courses.mymusic.component.feed.repository

import com.ixuea.courses.mymusic.BuildConfig
import com.ixuea.courses.mymusic.component.feed.model.Feed
import com.ixuea.courses.mymusic.model.Base
import com.ixuea.courses.mymusic.model.Resource
import com.ixuea.courses.mymusic.model.response.DetailResponse
import com.ixuea.courses.mymusic.model.response.ListResponse
import com.ixuea.courses.mymusic.repository.DefaultRepository
import com.luck.picture.lib.entity.LocalMedia
import java.io.File
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * Repository for feed publishing and media upload.
 */
class FeedPublishRepository private constructor(
    private val repository: DefaultRepository,
) {
    suspend fun uploadImages(data: List<LocalMedia>): ListResponse<Resource> {
        val bodyFiles = data.mapNotNull { media ->
            val path = mediaPath(media).takeIf { it.isNotBlank() } ?: return@mapNotNull null
            val file = File(path)
            val fileBody = file.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("file", file.name, fileBody)
        }

        val flavorBody = BuildConfig.FLAVOR.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        return repository.uploadFiles(bodyFiles, flavorBody)
    }

    suspend fun createFeed(data: Feed): DetailResponse<Base> {
        return repository.createFeed(data)
    }

    private fun mediaPath(data: LocalMedia): String {
        return data.compressPath.takeIf { !it.isNullOrBlank() } ?: data.path.orEmpty()
    }

    companion object {
        @Volatile
        private var instance: FeedPublishRepository? = null

        @JvmStatic
        fun getInstance(): FeedPublishRepository {
            return instance ?: synchronized(this) {
                instance ?: FeedPublishRepository(DefaultRepository.getInstance()).also {
                    instance = it
                }
            }
        }
    }
}
