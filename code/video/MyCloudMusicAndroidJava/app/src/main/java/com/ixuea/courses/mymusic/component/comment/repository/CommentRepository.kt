package com.ixuea.courses.mymusic.component.comment.repository

import com.ixuea.courses.mymusic.component.comment.model.Comment
import com.ixuea.courses.mymusic.model.Base
import com.ixuea.courses.mymusic.model.BaseId
import com.ixuea.courses.mymusic.model.response.DetailResponse
import com.ixuea.courses.mymusic.model.response.ListResponse
import com.ixuea.courses.mymusic.repository.DefaultRepository
import io.reactivex.rxjava3.core.Observable

class CommentRepository private constructor(
    private val repository: DefaultRepository,
) {
    fun comments(query: Map<String, String>): Observable<ListResponse<Comment>> {
        return repository.comments(query)
    }

    fun createComment(data: Comment): Observable<DetailResponse<Comment>> {
        return repository.createComment(data)
    }

    fun commentLike(id: String): Observable<DetailResponse<BaseId>> {
        return repository.commentLike(id)
    }

    fun cancelCommentLike(id: String): Observable<DetailResponse<Base>> {
        return repository.cancelCommentLike(id)
    }

    companion object {
        @Volatile
        private var instance: CommentRepository? = null

        @JvmStatic
        fun getInstance(): CommentRepository {
            return instance ?: synchronized(this) {
                instance ?: CommentRepository(DefaultRepository.getInstance()).also {
                    instance = it
                }
            }
        }
    }
}
