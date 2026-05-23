package com.ixuea.courses.mymusic.component.song.repository

import com.ixuea.courses.mymusic.component.song.model.Song
import com.ixuea.courses.mymusic.model.response.DetailResponse
import com.ixuea.courses.mymusic.repository.DefaultRepository

class SongRepository private constructor(
    private val repository: DefaultRepository,
) {
    suspend fun songDetail(id: String): DetailResponse<Song> {
        return repository.songDetail(id)
    }

    companion object {
        @Volatile
        private var instance: SongRepository? = null

        @JvmStatic
        fun getInstance(): SongRepository {
            return instance ?: synchronized(this) {
                instance ?: SongRepository(DefaultRepository.getInstance()).also {
                    instance = it
                }
            }
        }
    }
}
