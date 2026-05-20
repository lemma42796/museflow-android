package com.ixuea.courses.mymusic.component.sheet.repository

import com.ixuea.courses.mymusic.component.sheet.model.Sheet
import com.ixuea.courses.mymusic.model.Base
import com.ixuea.courses.mymusic.model.response.DetailResponse
import com.ixuea.courses.mymusic.repository.DefaultRepository
import io.reactivex.rxjava3.core.Observable

class SheetRepository private constructor(
    private val repository: DefaultRepository,
) {
    fun sheetDetail(id: String): Observable<DetailResponse<Sheet>> {
        return repository.sheetDetail(id)
    }

    fun collect(id: String): Observable<DetailResponse<Base>> {
        return repository.collect(id)
    }

    fun deleteCollect(id: String): Observable<DetailResponse<Base>> {
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
