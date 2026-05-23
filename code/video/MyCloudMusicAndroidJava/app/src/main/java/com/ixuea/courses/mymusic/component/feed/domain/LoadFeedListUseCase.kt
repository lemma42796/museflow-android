package com.ixuea.courses.mymusic.component.feed.domain

import com.ixuea.courses.mymusic.component.feed.model.Feed
import com.ixuea.courses.mymusic.component.feed.repository.FeedRepository

class LoadFeedListUseCase(
    private val repository: FeedRepository = FeedRepository.getInstance(),
) {
    suspend operator fun invoke(userId: String?): Result {
        return try {
            val response = repository.feeds(userId)
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
        data class Success(val feeds: List<Feed>) : Result
        data class Error(
            val message: String?,
            val error: Throwable?,
        ) : Result
    }
}
