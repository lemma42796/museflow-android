package com.ixuea.courses.mymusic.component.discovery.domain

import com.ixuea.courses.mymusic.component.discovery.model.DiscoveryPage
import com.ixuea.courses.mymusic.component.discovery.repository.DiscoveryRepository
import com.ixuea.courses.mymusic.util.PreferenceUtil

class LoadDiscoveryPageUseCase(
    private val repository: DiscoveryRepository = DiscoveryRepository.getInstance(),
) {
    suspend operator fun invoke(sp: PreferenceUtil): Result {
        return try {
            Result.Success(repository.homeSections(sp))
        } catch (error: Throwable) {
            Result.Error(error)
        }
    }

    sealed interface Result {
        data class Success(val page: DiscoveryPage) : Result
        data class Error(val error: Throwable) : Result
    }
}
