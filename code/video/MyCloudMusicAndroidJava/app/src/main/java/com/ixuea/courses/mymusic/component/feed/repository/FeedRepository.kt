package com.ixuea.courses.mymusic.component.feed.repository

import com.ixuea.courses.mymusic.component.feed.model.Feed
import com.ixuea.courses.mymusic.model.response.ListResponse
import com.ixuea.courses.mymusic.repository.DefaultRepository
import io.reactivex.rxjava3.core.Observable

/**
 * Repository for feed list refreshes.
 */
class FeedRepository private constructor(
    private val repository: DefaultRepository,
) {
    fun feeds(userId: String?): Observable<ListResponse<Feed>> {
        return repository.feeds(userId)
    }

    companion object {
        @Volatile
        private var instance: FeedRepository? = null

        @JvmStatic
        fun getInstance(): FeedRepository {
            return instance ?: synchronized(this) {
                instance ?: FeedRepository(DefaultRepository.getInstance()).also {
                    instance = it
                }
            }
        }
    }
}
