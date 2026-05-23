package com.ixuea.courses.mymusic.component.sheet.domain

import com.ixuea.courses.mymusic.component.sheet.model.Sheet
import com.ixuea.courses.mymusic.component.sheet.repository.SheetRepository

class LoadSheetDetailUseCase(
    private val repository: SheetRepository = SheetRepository.getInstance(),
) {
    suspend operator fun invoke(id: String): Result {
        return try {
            val response = repository.sheetDetail(id)
            val sheet = response.data
            if (response.isSucceeded() && sheet != null) {
                Result.Success(sheet)
            } else {
                Result.Error(response.message, null)
            }
        } catch (error: Throwable) {
            Result.Error(null, error)
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
