package com.ixuea.courses.mymusic.component.comment.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ixuea.courses.mymusic.component.comment.domain.CancelCommentLikeUseCase
import com.ixuea.courses.mymusic.component.comment.domain.CreateCommentUseCase
import com.ixuea.courses.mymusic.component.comment.domain.LikeCommentUseCase
import com.ixuea.courses.mymusic.component.comment.domain.LoadCommentsUseCase
import com.ixuea.courses.mymusic.component.comment.model.Comment
import com.ixuea.courses.mymusic.model.response.Meta
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CommentViewModel(
    private val loadComments: LoadCommentsUseCase = LoadCommentsUseCase(),
    private val createComment: CreateCommentUseCase = CreateCommentUseCase(),
    private val likeComment: LikeCommentUseCase = LikeCommentUseCase(),
    private val cancelCommentLike: CancelCommentLikeUseCase = CancelCommentLikeUseCase(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(CommentUiState())
    private val comments = mutableListOf<Comment>()
    private var pageMeta: Meta<Comment>? = null

    val uiState: StateFlow<CommentUiState> = _uiState

    fun refresh(sheetId: String?) {
        if (_uiState.value.isLoading) {
            return
        }

        pageMeta = null
        load(sheetId, CommentLoadOperation.REFRESH)
    }

    fun loadMore(sheetId: String?) {
        if (_uiState.value.isLoading) {
            return
        }

        load(sheetId, CommentLoadOperation.LOAD_MORE)
    }

    fun create(sheetId: String?, parentId: String?, content: String) {
        if (_uiState.value.isSubmitting) {
            return
        }

        _uiState.update {
            it.copy(
                isSubmitting = true,
                errorMessage = null,
                error = null,
            )
        }

        viewModelScope.launch {
            val param = Comment().apply {
                this.content = content
                this.sheetId = sheetId
                this.parentId = parentId
            }

            when (val result = createComment(param)) {
                is CreateCommentUseCase.Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            createCompleteVersion = it.createCompleteVersion + 1,
                        )
                    }
                }

                is CreateCommentUseCase.Result.Error -> publishError(
                    result.message,
                    result.error,
                    isSubmitting = false,
                )
            }
        }
    }

    fun toggleLike(comment: Comment) {
        if (_uiState.value.isLikeUpdating) {
            return
        }

        _uiState.update {
            it.copy(
                isLikeUpdating = true,
                errorMessage = null,
                error = null,
            )
        }

        viewModelScope.launch {
            val commentId = comment.id
            if (commentId.isNullOrBlank()) {
                publishError(null, null, isLikeUpdating = false)
                return@launch
            }

            if (comment.isLiked) {
                when (val result = cancelCommentLike(commentId)) {
                    is CancelCommentLikeUseCase.Result.Success -> {
                        comment.likeId = null
                        comment.likesCount = (comment.likesCount - 1).coerceAtLeast(0)
                        publishLikeUpdated()
                    }

                    is CancelCommentLikeUseCase.Result.Error -> publishError(
                        result.message,
                        result.error,
                        isLikeUpdating = false,
                    )
                }
            } else {
                when (val result = likeComment(commentId)) {
                    is LikeCommentUseCase.Result.Success -> {
                        comment.likeId = result.likeId
                        comment.likesCount = comment.likesCount + 1
                        publishLikeUpdated()
                    }

                    is LikeCommentUseCase.Result.Error -> publishError(
                        result.message,
                        result.error,
                        isLikeUpdating = false,
                    )
                }
            }
        }
    }

    private fun load(sheetId: String?, operation: CommentLoadOperation) {
        _uiState.update {
            it.copy(
                isLoading = true,
                loadOperation = operation,
                errorMessage = null,
                error = null,
            )
        }

        viewModelScope.launch {
            when (val result = loadComments(sheetId, pageMeta)) {
                is LoadCommentsUseCase.Result.Success -> {
                    pageMeta = result.meta
                    if (operation == CommentLoadOperation.REFRESH) {
                        comments.clear()
                    }
                    comments.addAll(result.comments)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            comments = comments.toList(),
                            pageComments = result.comments,
                            loadOperation = operation,
                            loadCompleteVersion = it.loadCompleteVersion + 1,
                            noMoreData = pageMeta?.next == null,
                        )
                    }
                }

                is LoadCommentsUseCase.Result.Error -> publishError(
                    result.message,
                    result.error,
                    isLoading = false,
                )
            }
        }
    }

    private fun publishLikeUpdated() {
        _uiState.update {
            it.copy(
                isLikeUpdating = false,
                comments = comments.toList(),
                likeUpdateVersion = it.likeUpdateVersion + 1,
            )
        }
    }

    private fun publishError(
        message: String?,
        error: Throwable?,
        isLoading: Boolean = _uiState.value.isLoading,
        isSubmitting: Boolean = _uiState.value.isSubmitting,
        isLikeUpdating: Boolean = _uiState.value.isLikeUpdating,
    ) {
        _uiState.update {
            it.copy(
                isLoading = isLoading,
                isSubmitting = isSubmitting,
                isLikeUpdating = isLikeUpdating,
                errorMessage = message,
                error = error,
                errorVersion = it.errorVersion + 1,
            )
        }
    }
}
