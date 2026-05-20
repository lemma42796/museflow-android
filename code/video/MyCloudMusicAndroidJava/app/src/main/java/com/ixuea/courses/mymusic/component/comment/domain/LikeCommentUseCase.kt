package com.ixuea.courses.mymusic.component.comment.domain

import com.ixuea.courses.mymusic.component.comment.repository.CommentRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LikeCommentUseCase(
    private val repository: CommentRepository = CommentRepository.getInstance(),
) {
    suspend operator fun invoke(id: String): Result {
        return suspendCancellableCoroutine { continuation ->
            val disposable = repository.commentLike(id).subscribe(
                { response ->
                    if (!continuation.isActive) {
                        return@subscribe
                    }

                    if (response.isSucceeded()) {
                        continuation.resume(Result.Success(response.data?.id))
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
        data class Success(val likeId: String?) : Result
        data class Error(
            val message: String?,
            val error: Throwable?,
        ) : Result
    }
}
