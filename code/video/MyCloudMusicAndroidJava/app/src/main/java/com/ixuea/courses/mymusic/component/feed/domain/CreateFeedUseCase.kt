package com.ixuea.courses.mymusic.component.feed.domain

import com.ixuea.courses.mymusic.component.feed.model.Feed
import com.ixuea.courses.mymusic.component.feed.repository.FeedPublishRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class CreateFeedUseCase(
    private val repository: FeedPublishRepository = FeedPublishRepository.getInstance(),
) {
    suspend operator fun invoke(data: Feed): Result {
        return suspendCancellableCoroutine { continuation ->
            val disposable = repository.createFeed(data).subscribe(
                { response ->
                    if (!continuation.isActive) {
                        return@subscribe
                    }

                    if (response.isSucceeded()) {
                        continuation.resume(Result.Success)
                    } else {
                        continuation.resume(Result.Error(response.message, null))
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
        data object Success : Result
        data class Error(
            val message: String?,
            val error: Throwable?,
        ) : Result
    }
}
