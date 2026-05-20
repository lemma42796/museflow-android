package com.ixuea.courses.mymusic.component.comment.domain

import com.ixuea.courses.mymusic.component.comment.model.Comment
import com.ixuea.courses.mymusic.component.comment.repository.CommentRepository
import com.ixuea.courses.mymusic.model.response.Meta
import com.ixuea.courses.mymusic.util.Constant
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LoadCommentsUseCase(
    private val repository: CommentRepository = CommentRepository.getInstance(),
) {
    suspend operator fun invoke(sheetId: String?, pageMeta: Meta<Comment>?): Result {
        val query = mutableMapOf<String, String>()
        if (!sheetId.isNullOrBlank()) {
            query[Constant.SHEET_ID] = sheetId
        }
        query[Constant.PAGE] = Meta.nextPage(pageMeta).toString()

        return suspendCancellableCoroutine { continuation ->
            val disposable = repository.comments(query).subscribe(
                { response ->
                    if (!continuation.isActive) {
                        return@subscribe
                    }

                    if (response.isSucceeded()) {
                        val meta = response.data
                        continuation.resume(
                            Result.Success(
                                meta = meta,
                                comments = meta?.data.orEmpty(),
                            )
                        )
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
