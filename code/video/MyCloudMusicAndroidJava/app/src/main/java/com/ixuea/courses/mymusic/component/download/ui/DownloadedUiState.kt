package com.ixuea.courses.mymusic.component.download.ui

import com.ixuea.courses.mymusic.component.song.model.Song

data class DownloadedUiState(
    val songs: List<Song> = emptyList(),
    val dataVersion: Long = 0,
)
