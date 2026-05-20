package com.ixuea.courses.mymusic.component.comment.ui

import com.ixuea.courses.mymusic.component.comment.model.Comment

data class CommentUiState(
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val isLikeUpdating: Boolean = false,
    val comments: List<Comment> = emptyList(),
    val pageComments: List<Comment> = emptyList(),
    val loadOperation: CommentLoadOperation = CommentLoadOperation.NONE,
    val loadCompleteVersion: Long = 0,
    val noMoreData: Boolean = false,
    val createCompleteVersion: Long = 0,
    val likeUpdateVersion: Long = 0,
    val errorMessage: String? = null,
    val error: Throwable? = null,
    val errorVersion: Long = 0,
)
