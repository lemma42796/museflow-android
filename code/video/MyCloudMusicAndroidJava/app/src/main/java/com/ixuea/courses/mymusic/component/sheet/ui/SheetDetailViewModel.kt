package com.ixuea.courses.mymusic.component.sheet.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ixuea.courses.mymusic.component.sheet.domain.CollectSheetUseCase
import com.ixuea.courses.mymusic.component.sheet.domain.DeleteSheetCollectUseCase
import com.ixuea.courses.mymusic.component.sheet.domain.LoadSheetDetailUseCase
import com.ixuea.courses.mymusic.component.sheet.model.Sheet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SheetDetailViewModel(
    private val loadSheetDetail: LoadSheetDetailUseCase = LoadSheetDetailUseCase(),
    private val collectSheet: CollectSheetUseCase = CollectSheetUseCase(),
    private val deleteSheetCollect: DeleteSheetCollectUseCase = DeleteSheetCollectUseCase(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(SheetDetailUiState())
    val uiState: StateFlow<SheetDetailUiState> = _uiState

    fun load(id: String) {
        if (id.isBlank() || _uiState.value.isLoading) {
            return
        }

        _uiState.update {
            it.copy(
                isLoading = true,
                errorMessage = null,
                error = null,
            )
        }

        viewModelScope.launch {
            when (val result = loadSheetDetail(id)) {
                is LoadSheetDetailUseCase.Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            sheet = result.sheet,
                            dataVersion = it.dataVersion + 1,
                        )
                    }
                }

                is LoadSheetDetailUseCase.Result.Error -> publishError(result.message, result.error)
            }
        }
    }

    fun toggleCollect(sheet: Sheet) {
        if (_uiState.value.isCollecting) {
            return
        }

        _uiState.update {
            it.copy(
                isCollecting = true,
                errorMessage = null,
                error = null,
            )
        }

        viewModelScope.launch {
            if (sheet.isCollect) {
                when (val result = deleteSheetCollect(sheet.id)) {
                    is DeleteSheetCollectUseCase.Result.Success -> publishCollectDeleted(sheet)
                    is DeleteSheetCollectUseCase.Result.Error -> publishError(result.message, result.error)
                }
            } else {
                when (val result = collectSheet(sheet.id)) {
                    is CollectSheetUseCase.Result.Success -> publishCollected(sheet)
                    is CollectSheetUseCase.Result.Error -> publishError(result.message, result.error)
                }
            }
        }
    }

    private fun publishCollected(sheet: Sheet) {
        sheet.collectId = "1"
        sheet.collectsCount = sheet.collectsCount + 1
        publishCollectResult(sheet, SheetCollectOperation.COLLECTED)
    }

    private fun publishCollectDeleted(sheet: Sheet) {
        sheet.collectId = null
        sheet.collectsCount = (sheet.collectsCount - 1).coerceAtLeast(0)
        publishCollectResult(sheet, SheetCollectOperation.UNCOLLECTED)
    }

    private fun publishCollectResult(sheet: Sheet, operation: SheetCollectOperation) {
        _uiState.update {
            it.copy(
                isCollecting = false,
                sheet = sheet,
                collectOperation = operation,
                collectEventVersion = it.collectEventVersion + 1,
            )
        }
    }

    private fun publishError(message: String?, error: Throwable?) {
        _uiState.update {
            it.copy(
                isLoading = false,
                isCollecting = false,
                errorMessage = message,
                error = error,
                errorVersion = it.errorVersion + 1,
            )
        }
    }
}
