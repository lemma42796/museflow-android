package com.ixuea.courses.mymusic.component.discovery.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ixuea.courses.mymusic.component.discovery.domain.LoadDiscoveryPageUseCase
import com.ixuea.courses.mymusic.util.PreferenceUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DiscoveryViewModel(
    private val loadDiscoveryPage: LoadDiscoveryPageUseCase = LoadDiscoveryPageUseCase(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(DiscoveryUiState())
    val uiState: StateFlow<DiscoveryUiState> = _uiState

    fun load(sp: PreferenceUtil) {
        if (_uiState.value.isLoading) {
            return
        }

        _uiState.update {
            it.copy(
                isLoading = true,
                error = null,
            )
        }

        viewModelScope.launch {
            when (val result = loadDiscoveryPage(sp)) {
                is LoadDiscoveryPageUseCase.Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            sections = result.page.sections,
                            dataVersion = it.dataVersion + 1,
                        )
                    }
                }

                is LoadDiscoveryPageUseCase.Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.error,
                            errorVersion = it.errorVersion + 1,
                        )
                    }
                }
            }
        }
    }
}
