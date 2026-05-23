package com.ixuea.courses.mymusic.component.song.domain

import com.ixuea.courses.mymusic.component.song.model.Song
import com.ixuea.courses.mymusic.component.song.repository.SongRepository

class LoadSongDetailUseCase(
    private val repository: SongRepository = SongRepository.getInstance(),
) {
    suspend operator fun invoke(id: String): Result {
        if (id.isBlank()) {
            return Result.Error(null, null)
        }

        return try {
            val response = repository.songDetail(id)
            val song = response.data
            if (response.isSucceeded() && song != null) {
                Result.Success(song)
            } else {
                Result.Error(response.message, null)
            }
        } catch (error: Throwable) {
            Result.Error(null, error)
        }
    }

    sealed interface Result {
        data class Success(val song: Song) : Result
        data class Error(
            val message: String?,
            val error: Throwable?,
        ) : Result
    }
}
