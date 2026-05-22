package com.ixuea.courses.mymusic.component.feed.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ixuea.courses.mymusic.component.feed.domain.LoadFeedListUseCase
import com.ixuea.courses.mymusic.component.feed.domain.ObserveFeedChangesUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FeedViewModel(
    private val loadFeedList: LoadFeedListUseCase = LoadFeedListUseCase(),
    private val observeFeedChanges: ObserveFeedChangesUseCase = ObserveFeedChangesUseCase(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState
    private var feedChangesJob: Job? = null

    fun observeChanges(userId: String?) {
        if (feedChangesJob?.isActive == true) {
            return
        }

        feedChangesJob = viewModelScope.launch {
            observeFeedChanges().collect {
                load(userId)
            }
        }
    }

    fun load(userId: String?) {
        if (_uiState.value.isLoading) {
            return
        }

        _uiState.update {
            it.copy(
                isLoading = true,
                errorMessage = null,
                error = null,
            )
        }

        viewModelScope.launch {
            when (val result = loadFeedList(userId)) {
                is LoadFeedListUseCase.Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            feeds = result.feeds,
                            dataVersion = it.dataVersion + 1,
                        )
                    }
                }

                is LoadFeedListUseCase.Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message,
                            error = result.error,
                            errorVersion = it.errorVersion + 1,
                        )
                    }
                }
            }
        }
    }
}
