package com.ixuea.courses.mymusic.component.sheet.ui

import com.ixuea.courses.mymusic.component.sheet.model.Sheet

data class SheetDetailUiState(
    val isLoading: Boolean = false,
    val isCollecting: Boolean = false,
    val sheet: Sheet? = null,
    val dataVersion: Long = 0,
    val errorMessage: String? = null,
    val error: Throwable? = null,
    val errorVersion: Long = 0,
    val collectOperation: SheetCollectOperation = SheetCollectOperation.NONE,
    val collectEventVersion: Long = 0,
)
