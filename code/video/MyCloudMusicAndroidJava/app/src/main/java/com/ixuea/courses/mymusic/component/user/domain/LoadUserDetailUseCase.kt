package com.ixuea.courses.mymusic.component.user.domain

import com.ixuea.courses.mymusic.component.user.model.User
import com.ixuea.courses.mymusic.component.user.repository.UserRepository

class LoadUserDetailUseCase(
    private val repository: UserRepository = UserRepository.getInstance(),
) {
    suspend operator fun invoke(userId: String): Result {
        if (userId.isBlank()) {
            return Result.Error(null, null)
        }

        repository.cachedUser(userId)?.let { cachedUser ->
            return Result.Success(cachedUser)
        }

        return try {
            val response = repository.userDetail(userId)
            val user = response.data
            if (response.isSucceeded() && user != null) {
                Result.Success(user)
            } else {
                Result.Error(response.message, null)
            }
        } catch (error: Throwable) {
            Result.Error(null, error)
        }
    }

    sealed interface Result {
        data class Success(val user: User) : Result
        data class Error(
            val message: String?,
            val error: Throwable?,
        ) : Result
    }
}
