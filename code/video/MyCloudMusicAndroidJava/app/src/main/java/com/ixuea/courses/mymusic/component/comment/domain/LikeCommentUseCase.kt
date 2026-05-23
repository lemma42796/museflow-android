package com.ixuea.courses.mymusic.component.comment.domain

import com.ixuea.courses.mymusic.component.comment.repository.CommentRepository

class LikeCommentUseCase(
    private val repository: CommentRepository = CommentRepository.getInstance(),
) {
    suspend operator fun invoke(id: String): Result {
        return try {
            val response = repository.commentLike(id)
            if (response.isSucceeded()) {
                Result.Success(response.data?.id)
            } else {
                Result.Error(response.message, null)
            }
        } catch (error: Throwable) {
            Result.Error(null, error)
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
