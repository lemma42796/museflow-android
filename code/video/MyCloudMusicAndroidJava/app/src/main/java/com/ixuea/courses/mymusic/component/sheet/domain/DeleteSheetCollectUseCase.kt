package com.ixuea.courses.mymusic.component.sheet.domain

import com.ixuea.courses.mymusic.component.sheet.repository.SheetRepository

class DeleteSheetCollectUseCase(
    private val repository: SheetRepository = SheetRepository.getInstance(),
) {
    suspend operator fun invoke(id: String): Result {
        return try {
            val response = repository.deleteCollect(id)
            if (response.isSucceeded()) {
                Result.Success
            } else {
                Result.Error(response.message, null)
            }
        } catch (error: Throwable) {
            Result.Error(null, error)
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
