package com.ixuea.courses.mymusic.component.feed.domain

import com.ixuea.courses.mymusic.component.feed.repository.FeedPublishRepository
import com.ixuea.courses.mymusic.model.Resource
import com.luck.picture.lib.entity.LocalMedia

class UploadFeedImagesUseCase(
    private val repository: FeedPublishRepository = FeedPublishRepository.getInstance(),
) {
    suspend operator fun invoke(data: List<LocalMedia>): Result {
        return try {
            val response = repository.uploadImages(data)
            if (response.isSucceeded()) {
                Result.Success(response.data?.data.orEmpty())
            } else {
                Result.Error(response.message, null)
            }
        } catch (error: Throwable) {
            Result.Error(null, error)
        }
    }

    sealed interface Result {
        data class Success(val resources: List<Resource>) : Result
        data class Error(
            val message: String?,
            val error: Throwable?,
        ) : Result
    }
}
