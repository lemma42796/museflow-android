package com.ixuea.courses.mymusic.component.user.repository

import com.ixuea.courses.mymusic.component.user.model.User
import com.ixuea.courses.mymusic.model.response.DetailResponse
import com.ixuea.courses.mymusic.repository.DefaultRepository
import io.reactivex.rxjava3.core.Observable
import java.util.concurrent.ConcurrentHashMap

class UserRepository private constructor(
    private val repository: DefaultRepository,
) {
    private val userCaches = ConcurrentHashMap<String, User>()

    fun cachedUser(userId: String): User? {
        return userCaches[userId]
    }

    fun userDetail(userId: String): Observable<DetailResponse<User>> {
        return repository.userDetail(userId)
            .doOnNext { response ->
                response.data?.let { user ->
                    userCaches[userId] = user
                }
            }
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null

        @JvmStatic
        fun getInstance(): UserRepository {
            return instance ?: synchronized(this) {
                instance ?: UserRepository(DefaultRepository.getInstance()).also {
                    instance = it
                }
            }
        }
    }
}
