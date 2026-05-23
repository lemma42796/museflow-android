package com.ixuea.courses.mymusic.component.music.domain

import android.content.Context
import com.ixuea.courses.mymusic.component.music.repository.LocalMusicScanRepository
import com.ixuea.courses.mymusic.component.song.model.Song

class ScanLocalMusicUseCase(
    private val repository: LocalMusicScanRepository = LocalMusicScanRepository.getInstance(),
) {
    suspend operator fun invoke(
        context: Context,
        onProgress: suspend (String) -> Unit,
    ): List<Song> {
        return repository.scan(context, onProgress)
    }
}
