package com.ixuea.courses.mymusic.component.download.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ixuea.courses.mymusic.component.download.domain.LoadDownloadedSongsUseCase
import com.ixuea.courses.mymusic.component.download.domain.ObserveDownloadedChangesUseCase
import com.ixuea.courses.mymusic.util.LiteORMUtil
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DownloadedViewModel(
    private val loadDownloadedSongs: LoadDownloadedSongsUseCase = LoadDownloadedSongsUseCase(),
    private val observeDownloadedChangesUseCase: ObserveDownloadedChangesUseCase = ObserveDownloadedChangesUseCase(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(DownloadedUiState())
    val uiState: StateFlow<DownloadedUiState> = _uiState
    private var downloadedChangesJob: Job? = null

    fun observeDownloadedChanges(orm: LiteORMUtil) {
        if (downloadedChangesJob?.isActive == true) {
            return
        }

        downloadedChangesJob = viewModelScope.launch {
            observeDownloadedChangesUseCase().collect {
                load(orm)
            }
        }
    }

    fun load(orm: LiteORMUtil) {
        val songs = loadDownloadedSongs(orm)
        _uiState.update {
            it.copy(
                songs = songs,
                dataVersion = it.dataVersion + 1,
            )
        }
    }
}
