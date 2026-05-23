package com.ixuea.courses.mymusic.component.comment.domain

import com.ixuea.courses.mymusic.component.comment.model.Comment
import com.ixuea.courses.mymusic.component.comment.repository.CommentRepository

class CreateCommentUseCase(
    private val repository: CommentRepository = CommentRepository.getInstance(),
) {
    suspend operator fun invoke(data: Comment): Result {
        return try {
            val response = repository.createComment(data)
            if (response.isSucceeded()) {
                Result.Success(response.data)
            } else {
                Result.Error(response.message, null)
            }
        } catch (error: Throwable) {
            Result.Error(null, error)
        }
    }

    sealed interface Result {
        data class Success(val comment: Comment?) : Result
        data class Error(
            val message: String?,
            val error: Throwable?,
        ) : Result
    }
}
