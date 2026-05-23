package com.ixuea.courses.mymusic.component.comment.domain

import com.ixuea.courses.mymusic.component.comment.model.Comment
import com.ixuea.courses.mymusic.component.comment.repository.CommentRepository
import com.ixuea.courses.mymusic.model.response.Meta
import com.ixuea.courses.mymusic.util.Constant

class LoadCommentsUseCase(
    private val repository: CommentRepository = CommentRepository.getInstance(),
) {
    suspend operator fun invoke(sheetId: String?, pageMeta: Meta<Comment>?): Result {
        val query = mutableMapOf<String, String>()
        if (!sheetId.isNullOrBlank()) {
            query[Constant.SHEET_ID] = sheetId
        }
        query[Constant.PAGE] = Meta.nextPage(pageMeta).toString()

        return try {
            val response = repository.comments(query)
            if (response.isSucceeded()) {
                val meta = response.data
                Result.Success(
                    meta = meta,
                    comments = meta?.data.orEmpty(),
                )
            } else {
                Result.Error(response.message, null)
            }
        } catch (error: Throwable) {
            Result.Error(null, error)
        }
    }

    sealed interface Result {
        data class Success(
            val meta: Meta<Comment>?,
            val comments: List<Comment>,
        ) : Result

        data class Error(
            val message: String?,
            val error: Throwable?,
        ) : Result
    }
}
