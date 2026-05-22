package com.ixuea.courses.mymusic.component.user.domain

import com.ixuea.courses.mymusic.component.user.model.User
import com.ixuea.courses.mymusic.component.user.repository.UserRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

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

        return suspendCancellableCoroutine { continuation ->
            val disposable = repository.userDetail(userId).subscribe(
                { response ->
                    if (!continuation.isActive) {
                        return@subscribe
                    }

                    val user = response.data
                    if (response.isSucceeded() && user != null) {
                        continuation.resume(Result.Success(user))
                    } else {
                        continuation.resume(Result.Error(response.message, null))
                    }
                },
                { error ->
                    if (continuation.isActive) {
                        continuation.resume(Result.Error(null, error))
                    }
                },
            )
            continuation.invokeOnCancellation {
                disposable.dispose()
            }
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
