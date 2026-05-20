package com.ixuea.courses.mymusic.component.sheet.domain

import com.ixuea.courses.mymusic.component.sheet.repository.SheetRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class DeleteSheetCollectUseCase(
    private val repository: SheetRepository = SheetRepository.getInstance(),
) {
    suspend operator fun invoke(id: String): Result {
        return suspendCancellableCoroutine { continuation ->
            val disposable = repository.deleteCollect(id).subscribe(
                { response ->
                    if (!continuation.isActive) {
                        return@subscribe
                    }

                    if (response.isSucceeded()) {
                        continuation.resume(Result.Success)
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
        data object Success : Result
        data class Error(
            val message: String?,
            val error: Throwable?,
        ) : Result
    }
}
