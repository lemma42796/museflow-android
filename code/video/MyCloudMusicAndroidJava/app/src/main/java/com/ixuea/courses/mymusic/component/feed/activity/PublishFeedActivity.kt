package com.ixuea.courses.mymusic.component.feed.activity

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelProvider
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.activity.BaseLogicActivity
import com.ixuea.courses.mymusic.component.feed.domain.CompressFeedImagesUseCase
import com.ixuea.courses.mymusic.component.feed.model.Feed
import com.ixuea.courses.mymusic.component.feed.ui.FeedPublishOperation
import com.ixuea.courses.mymusic.component.feed.ui.FeedPublishScreen
import com.ixuea.courses.mymusic.component.feed.ui.FeedPublishUiState
import com.ixuea.courses.mymusic.component.feed.ui.FeedPublishViewModel
import com.ixuea.courses.mymusic.config.glide.GlideEngine
import com.ixuea.courses.mymusic.ui.compose.MuseFlowTheme
import com.ixuea.superui.toast.SuperToast
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.config.SelectModeConfig
import com.luck.picture.lib.engine.CompressFileEngine
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnKeyValueResultCallbackListener
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import timber.log.Timber
import java.util.ArrayList

/**
 * Feed publishing screen backed by Compose.
 */
class PublishFeedActivity : BaseLogicActivity() {
    private val feed = Feed()
    private val compressFeedImages = CompressFeedImagesUseCase()
    private lateinit var viewModel: FeedPublishViewModel
    private var currentOperation = FeedPublishOperation.NONE
    private var handledRequestErrorVersion = 0L
    private var handledUploadCountErrorVersion = 0L
    private var handledPublishCompleteVersion = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[FeedPublishViewModel::class.java]
        viewModel.setSelectedImages(emptyList())

        setContent {
            val state by viewModel.uiState.collectAsState()
            var content by rememberSaveable { mutableStateOf("") }

            LaunchedEffect(state.operation) {
                renderOperation(state.operation)
            }
            LaunchedEffect(state.requestErrorVersion) {
                renderRequestError(state)
            }
            LaunchedEffect(state.uploadCountErrorVersion) {
                renderUploadCountError(state)
            }
            LaunchedEffect(state.publishCompleteVersion) {
                renderPublishComplete(state)
            }

            MuseFlowTheme {
                FeedPublishScreen(
                    state = state,
                    content = content,
                    onContentChange = { content = it },
                    onSelectImage = ::selectImage,
                    onRemoveImage = viewModel::removeSelectedImage,
                    onPublish = ::sendClick,
                    onBack = { onBackPressedDispatcher.onBackPressed() },
                )
            }
        }
    }

    private fun selectImage() {
        PictureSelector.create(this)
            .openGallery(SelectMimeType.ofImage())
            .setImageEngine(GlideEngine.createGlideEngine())
            .setMaxSelectNum(9)
            .setMinSelectNum(1)
            .setImageSpanCount(3)
            .setSelectionMode(SelectModeConfig.MULTIPLE)
            .isPreviewImage(true)
            .isDisplayCamera(true)
            .setCameraImageFormat(PictureMimeType.JPEG)
            .setCompressEngine(object : CompressFileEngine {
                override fun onStartCompress(
                    context: Context,
                    source: ArrayList<Uri>,
                    call: OnKeyValueResultCallbackListener
                ) {
                    compressFeedImages(
                        context,
                        source,
                        onComplete = { originalFilePath, compressedFilePath ->
                            call.onCallback(originalFilePath, compressedFilePath)
                        }
                    )
                }
            })
            .forResult(object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: ArrayList<LocalMedia>) {
                    viewModel.setSelectedImages(result)
                }

                override fun onCancel() {
                }
            })
    }

    private fun sendClick(content: String) {
        val trimmed = content.trim()

        if (trimmed.isBlank()) {
            SuperToast.error(R.string.hint_feed)
            return
        }

        if (trimmed.length > MAX_CONTENT_LENGTH) {
            SuperToast.error(R.string.error_content_length)
            return
        }

        feed.content = "%s\n📱来自【Android Java云音乐客户端】".format(trimmed)
        viewModel.publish(feed)
    }

    private fun renderOperation(operation: FeedPublishOperation) {
        if (currentOperation == operation) {
            return
        }

        currentOperation = operation
        when (operation) {
            FeedPublishOperation.NONE -> hideLoading()
            FeedPublishOperation.UPLOADING_IMAGES -> showLoading(getString(R.string.loading_upload, 1))
            FeedPublishOperation.CREATING_FEED -> showLoading(getString(R.string.loading))
        }
    }

    private fun renderRequestError(state: FeedPublishUiState) {
        if (state.requestErrorVersion == handledRequestErrorVersion) {
            return
        }

        handledRequestErrorVersion = state.requestErrorVersion
        if (state.requestError != null) {
            Timber.e(state.requestError, "publish feed error %s", state.requestErrorMessage)
        } else {
            Timber.e("publish feed error %s", state.requestErrorMessage)
        }
        val message = state.requestErrorMessage
            ?.takeIf { it.isNotBlank() }
            ?: getString(R.string.error_upload_image)
        SuperToast.show(message)
    }

    private fun renderUploadCountError(state: FeedPublishUiState) {
        if (state.uploadCountErrorVersion == handledUploadCountErrorVersion) {
            return
        }

        handledUploadCountErrorVersion = state.uploadCountErrorVersion
        SuperToast.show(R.string.error_upload_image)
    }

    private fun renderPublishComplete(state: FeedPublishUiState) {
        if (state.publishCompleteVersion == handledPublishCompleteVersion) {
            return
        }

        handledPublishCompleteVersion = state.publishCompleteVersion
        finish()
    }

    companion object {
        private const val MAX_CONTENT_LENGTH = 140
    }
}
