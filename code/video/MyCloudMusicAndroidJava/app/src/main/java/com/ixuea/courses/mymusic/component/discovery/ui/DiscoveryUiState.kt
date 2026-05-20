package com.ixuea.courses.mymusic.component.discovery.ui

import com.ixuea.courses.mymusic.model.ui.BaseMultiItemEntity

data class DiscoveryUiState(
    val isLoading: Boolean = false,
    val sections: List<BaseMultiItemEntity> = emptyList(),
    val dataVersion: Long = 0,
    val error: Throwable? = null,
    val errorVersion: Long = 0,
)
