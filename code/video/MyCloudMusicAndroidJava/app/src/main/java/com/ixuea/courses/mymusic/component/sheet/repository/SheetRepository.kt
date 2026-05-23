package com.ixuea.courses.mymusic.component.sheet.repository

import com.ixuea.courses.mymusic.component.sheet.model.Sheet
import com.ixuea.courses.mymusic.model.Base
import com.ixuea.courses.mymusic.model.response.DetailResponse
import com.ixuea.courses.mymusic.repository.DefaultRepository

class SheetRepository private constructor(
    private val repository: DefaultRepository,
) {
    suspend fun sheetDetail(id: String): DetailResponse<Sheet> {
        return repository.sheetDetail(id)
    }

    suspend fun collect(id: String): DetailResponse<Base> {
        return repository.collect(id)
    }

    suspend fun deleteCollect(id: String): DetailResponse<Base> {
        return repository.deleteCollect(id)
    }

    companion object {
        @Volatile
        private var instance: SheetRepository? = null

        @JvmStatic
        fun getInstance(): SheetRepository {
            return instance ?: synchronized(this) {
                instance ?: SheetRepository(DefaultRepository.getInstance()).also {
                    instance = it
                }
            }
        }
    }
}
