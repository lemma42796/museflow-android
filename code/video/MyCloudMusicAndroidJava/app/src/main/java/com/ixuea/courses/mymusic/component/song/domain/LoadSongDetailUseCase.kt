package com.ixuea.courses.mymusic.component.song.domain

import com.ixuea.courses.mymusic.component.song.model.Song
import com.ixuea.courses.mymusic.component.song.repository.SongRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LoadSongDetailUseCase(
    private val repository: SongRepository = SongRepository.getInstance(),
) {
    suspend operator fun invoke(id: String): Result {
        if (id.isBlank()) {
            return Result.Error(null, null)
        }

        return suspendCancellableCoroutine { continuation ->
            val disposable = repository.songDetail(id).subscribe(
                { response ->
                    if (!continuation.isActive) {
                        return@subscribe
                    }

                    val song = response.data
                    if (response.isSucceeded() && song != null) {
                        continuation.resume(Result.Success(song))
                    } else {
                        continuation.resume(Result.Error(response.message, null))
                    }
                },
                { error ->
                    if (continuation.isActive) {
                        continuation.resume(Result.Error(null, error))
                    }
                },
            )
            continuation.invokeOnCancellation {
                disposable.dispose()
            }
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
