package com.ixuea.courses.mymusic.component.feed.domain

import com.ixuea.courses.mymusic.component.feed.model.Feed
import com.ixuea.courses.mymusic.component.feed.repository.FeedPublishRepository

class CreateFeedUseCase(
    private val repository: FeedPublishRepository = FeedPublishRepository.getInstance(),
) {
    suspend operator fun invoke(data: Feed): Result {
        return try {
            val response = repository.createFeed(data)
            if (response.isSucceeded()) {
                Result.Success
            } else {
                Result.Error(response.message, null)
            }
        } catch (error: Throwable) {
            Result.Error(null, error)
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
