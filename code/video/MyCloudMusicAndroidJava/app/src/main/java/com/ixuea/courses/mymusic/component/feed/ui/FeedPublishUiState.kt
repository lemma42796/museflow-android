package com.ixuea.courses.mymusic.component.feed.ui

data class FeedPublishUiState(
    val mediaItems: List<Any> = emptyList(),
    val operation: FeedPublishOperation = FeedPublishOperation.NONE,
    val requestErrorMessage: String? = null,
    val requestError: Throwable? = null,
    val requestErrorVersion: Long = 0,
    val uploadCountErrorVersion: Long = 0,
    val publishCompleteVersion: Long = 0,
)
