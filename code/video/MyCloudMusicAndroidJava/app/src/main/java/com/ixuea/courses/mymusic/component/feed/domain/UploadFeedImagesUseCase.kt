package com.ixuea.courses.mymusic.component.feed.domain

import com.ixuea.courses.mymusic.component.feed.repository.FeedPublishRepository
import com.ixuea.courses.mymusic.model.Resource
import com.luck.picture.lib.entity.LocalMedia
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class UploadFeedImagesUseCase(
    private val repository: FeedPublishRepository = FeedPublishRepository.getInstance(),
) {
    suspend operator fun invoke(data: List<LocalMedia>): Result {
        return suspendCancellableCoroutine { continuation ->
            val disposable = repository.uploadImages(data).subscribe(
                { response ->
                    if (!continuation.isActive) {
                        return@subscribe
                    }

                    if (response.isSucceeded()) {
                        val resources = response.getData()?.getData().orEmpty()
                        continuation.resume(Result.Success(resources))
                    } else {
                        continuation.resume(Result.Error(response.getMessage(), null))
                    }
                },
                { error ->
                    if (continuation.isActive) {
                        continuation.resume(Result.Error(null, error))
                    }
                }
            )
            continuation.invokeOnCancellation {
                disposable.dispose()
            }
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
