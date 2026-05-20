package com.ixuea.courses.mymusic.component.feed.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.component.feed.domain.CreateFeedUseCase
import com.ixuea.courses.mymusic.component.feed.domain.UploadFeedImagesUseCase
import com.ixuea.courses.mymusic.component.feed.model.Feed
import com.ixuea.courses.mymusic.model.Resource
import com.luck.picture.lib.entity.LocalMedia
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Holds publish-screen media selection and publishing state outside the Activity.
 */
class FeedPublishViewModel(
    private val uploadFeedImages: UploadFeedImagesUseCase = UploadFeedImagesUseCase(),
    private val createFeed: CreateFeedUseCase = CreateFeedUseCase(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(FeedPublishUiState())
    private val selectedImages = mutableListOf<LocalMedia>()

    val uiState: StateFlow<FeedPublishUiState> = _uiState

    fun setSelectedImages(data: List<LocalMedia>?) {
        selectedImages.clear()
        data?.let(selectedImages::addAll)
        publishMediaItems()
    }

    fun removeSelectedImage(position: Int) {
        if (position in selectedImages.indices) {
            selectedImages.removeAt(position)
            publishMediaItems()
        }
    }

    fun getSelectedImages(): List<LocalMedia> {
        return selectedImages.toList()
    }

    fun publish(feed: Feed) {
        if (_uiState.value.operation != FeedPublishOperation.NONE) {
            return
        }

        val images = selectedImages.toList()
        viewModelScope.launch {
            if (images.isNotEmpty()) {
                _uiState.update {
                    it.copy(
                        operation = FeedPublishOperation.UPLOADING_IMAGES,
                        requestErrorMessage = null,
                        requestError = null,
                    )
                }

                when (val result = uploadFeedImages(images)) {
                    is UploadFeedImagesUseCase.Result.Success -> {
                        if (result.resources.size == images.size) {
                            create(feed, result.resources)
                        } else {
                            publishUploadCountError()
                        }
                    }

                    is UploadFeedImagesUseCase.Result.Error -> publishRequestError(
                        result.message,
                        result.error,
                    )
                }
            } else {
                create(feed, null)
            }
        }
    }

    private suspend fun create(feed: Feed, resources: List<Resource>?) {
        _uiState.update {
            it.copy(
                operation = FeedPublishOperation.CREATING_FEED,
                requestErrorMessage = null,
                requestError = null,
            )
        }

        feed.medias = resources
        when (val result = createFeed(feed)) {
            is CreateFeedUseCase.Result.Success -> {
                _uiState.update {
                    it.copy(
                        operation = FeedPublishOperation.NONE,
                        publishCompleteVersion = it.publishCompleteVersion + 1,
                    )
                }
            }

            is CreateFeedUseCase.Result.Error -> publishRequestError(
                result.message,
                result.error,
            )
        }
    }

    private fun publishRequestError(message: String?, error: Throwable?) {
        _uiState.update {
            it.copy(
                operation = FeedPublishOperation.NONE,
                requestErrorMessage = message,
                requestError = error,
                requestErrorVersion = it.requestErrorVersion + 1,
            )
        }
    }

    private fun publishUploadCountError() {
        _uiState.update {
            it.copy(
                operation = FeedPublishOperation.NONE,
                uploadCountErrorVersion = it.uploadCountErrorVersion + 1,
            )
        }
    }

    private fun publishMediaItems() {
        val items = selectedImages.toMutableList<Any>()
        if (items.size < 9) {
            items += R.drawable.add_fill
        }
        _uiState.update {
            it.copy(mediaItems = items)
        }
    }
}
