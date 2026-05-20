package com.ixuea.courses.mymusic.component.discovery.domain

import com.ixuea.courses.mymusic.component.discovery.model.DiscoveryPage
import com.ixuea.courses.mymusic.component.discovery.repository.DiscoveryRepository
import com.ixuea.courses.mymusic.util.PreferenceUtil
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LoadDiscoveryPageUseCase(
    private val repository: DiscoveryRepository = DiscoveryRepository.getInstance(),
) {
    suspend operator fun invoke(sp: PreferenceUtil): Result {
        return suspendCancellableCoroutine { continuation ->
            val disposable = repository.homeSections(sp).subscribe(
                { page ->
                    if (continuation.isActive) {
                        continuation.resume(Result.Success(page))
                    }
                },
                { error ->
                    if (continuation.isActive) {
                        continuation.resume(Result.Error(error))
                    }
                }
            )
            continuation.invokeOnCancellation {
                disposable.dispose()
            }
        }
    }

    sealed interface Result {
        data class Success(val page: DiscoveryPage) : Result
        data class Error(val error: Throwable) : Result
    }
}
