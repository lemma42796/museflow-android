package com.ixuea.courses.mymusic.component.feed.ui

import com.ixuea.courses.mymusic.component.feed.model.Feed

data class FeedUiState(
    val isLoading: Boolean = false,
    val feeds: List<Feed> = emptyList(),
    val dataVersion: Long = 0,
    val errorMessage: String? = null,
    val error: Throwable? = null,
    val errorVersion: Long = 0,
)
