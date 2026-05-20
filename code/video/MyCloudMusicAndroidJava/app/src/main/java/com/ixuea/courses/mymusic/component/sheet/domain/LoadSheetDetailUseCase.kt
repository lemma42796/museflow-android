package com.ixuea.courses.mymusic.component.sheet.domain

import com.ixuea.courses.mymusic.component.sheet.model.Sheet
import com.ixuea.courses.mymusic.component.sheet.repository.SheetRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LoadSheetDetailUseCase(
    private val repository: SheetRepository = SheetRepository.getInstance(),
) {
    suspend operator fun invoke(id: String): Result {
        return suspendCancellableCoroutine { continuation ->
            val disposable = repository.sheetDetail(id).subscribe(
                { response ->
                    if (!continuation.isActive) {
                        return@subscribe
                    }

                    if (response.isSucceeded()) {
                        val sheet = response.data
                        if (sheet != null) {
                            continuation.resume(Result.Success(sheet))
                        } else {
                            continuation.resume(Result.Error(response.message, null))
                        }
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
        data class Success(val sheet: Sheet) : Result
        data class Error(
            val message: String?,
            val error: Throwable?,
        ) : Result
    }
}
