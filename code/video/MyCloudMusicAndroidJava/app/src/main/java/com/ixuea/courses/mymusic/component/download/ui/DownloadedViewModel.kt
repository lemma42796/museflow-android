package com.ixuea.courses.mymusic.component.download.ui

import androidx.lifecycle.ViewModel
import com.ixuea.courses.mymusic.component.download.domain.LoadDownloadedSongsUseCase
import com.ixuea.courses.mymusic.util.LiteORMUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class DownloadedViewModel(
    private val loadDownloadedSongs: LoadDownloadedSongsUseCase = LoadDownloadedSongsUseCase(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(DownloadedUiState())
    val uiState: StateFlow<DownloadedUiState> = _uiState

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
