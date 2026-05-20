package com.ixuea.courses.mymusic.component.feed.domain

import com.ixuea.courses.mymusic.component.feed.model.Feed
import com.ixuea.courses.mymusic.component.feed.repository.FeedRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LoadFeedListUseCase(
    private val repository: FeedRepository = FeedRepository.getInstance(),
) {
    suspend operator fun invoke(userId: String?): Result {
        return suspendCancellableCoroutine { continuation ->
            val disposable = repository.feeds(userId).subscribe(
                { response ->
                    if (!continuation.isActive) {
                        return@subscribe
                    }

                    if (response.isSucceeded()) {
                        val feeds = response.getData()?.getData().orEmpty()
                        continuation.resume(Result.Success(feeds))
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
        data class Success(val feeds: List<Feed>) : Result
        data class Error(
            val message: String?,
            val error: Throwable?,
        ) : Result
    }
}
