package com.ixuea.courses.mymusic.component.download.domain

import com.ixuea.courses.mymusic.component.download.repository.DownloadRepository
import com.ixuea.courses.mymusic.component.song.model.Song
import com.ixuea.courses.mymusic.util.LiteORMUtil

class LoadDownloadedSongsUseCase(
    private val repository: DownloadRepository = DownloadRepository.getInstance(),
) {
    operator fun invoke(orm: LiteORMUtil): List<Song> {
        return repository.findDownloadedSongs(orm)
    }
}
