package com.ixuea.courses.mymusic.component.comment.repository

import com.ixuea.courses.mymusic.component.comment.model.Comment
import com.ixuea.courses.mymusic.model.Base
import com.ixuea.courses.mymusic.model.BaseId
import com.ixuea.courses.mymusic.model.response.DetailResponse
import com.ixuea.courses.mymusic.model.response.ListResponse
import com.ixuea.courses.mymusic.repository.DefaultRepository

class CommentRepository private constructor(
    private val repository: DefaultRepository,
) {
    suspend fun comments(query: Map<String, String>): ListResponse<Comment> {
        return repository.comments(query)
    }

    suspend fun createComment(data: Comment): DetailResponse<Comment> {
        return repository.createComment(data)
    }

    suspend fun commentLike(id: String): DetailResponse<BaseId> {
        return repository.commentLike(id)
    }

    suspend fun cancelCommentLike(id: String): DetailResponse<Base> {
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
