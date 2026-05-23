package com.ixuea.courses.mymusic.component.chat.activity

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelProvider
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.activity.BaseLogicActivity
import com.ixuea.courses.mymusic.component.chat.ui.ChatScreen
import com.ixuea.courses.mymusic.component.chat.ui.ChatUiState
import com.ixuea.courses.mymusic.component.chat.ui.ChatViewModel
import com.ixuea.courses.mymusic.config.glide.GlideEngine
import com.ixuea.courses.mymusic.ui.compose.MuseFlowTheme
import com.ixuea.courses.mymusic.util.Constant
import com.ixuea.superui.toast.SuperToast
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.config.SelectModeConfig
import com.luck.picture.lib.engine.CompressFileEngine
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnKeyValueResultCallbackListener
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import java.io.File
import java.util.ArrayList
import org.apache.commons.lang3.StringUtils
import timber.log.Timber
import top.zibin.luban.Luban
import top.zibin.luban.OnNewCompressListener

/**
 * 聊天界面
 */
class ChatActivity : BaseLogicActivity() {
    private var targetId: String = ""
    private lateinit var viewModel: ChatViewModel
    private var inputContent by mutableStateOf("")
    private var handledErrorVersion = 0L
    private var handledSendErrorVersion = 0L
    private var handledUnreadClearErrorVersion = 0L
    private var handledClearInputVersion = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        targetId = extraId().orEmpty()
        viewModel = ViewModelProvider(this)[ChatViewModel::class.java]
        viewModel.observeIncomingMessages(targetId)

        setContent {
            val state by viewModel.uiState.collectAsState()

            LaunchedEffect(state.errorVersion) {
                renderHistoryError(state)
            }
            LaunchedEffect(state.sendErrorVersion) {
                renderSendError(state)
            }
            LaunchedEffect(state.unreadClearErrorVersion) {
                renderUnreadClearError(state)
            }
            LaunchedEffect(state.clearInputVersion) {
                clearInput(state.clearInputVersion)
            }

            MuseFlowTheme {
                ChatScreen(
                    state = state,
                    inputText = inputContent,
                    onInputChange = { inputContent = it },
                    onBack = { onBackPressedDispatcher.onBackPressed() },
                    onLoadMore = ::loadMore,
                    onSelectImage = ::selectImage,
                    onSendText = ::sendTextMessage,
                )
            }
        }
    }

    override fun initDatum() {
        super.initDatum()
        loadData()
    }

    fun selectImage() {
        PictureSelector.create(this)
            .openGallery(SelectMimeType.ofImage())
            .setImageEngine(GlideEngine.createGlideEngine())
            .setMaxSelectNum(1)
            .setMinSelectNum(1)
            .setImageSpanCount(3)
            .setSelectionMode(SelectModeConfig.SINGLE)
            .isPreviewImage(true)
            .isDisplayCamera(true)
            .setCameraImageFormat(PictureMimeType.JPEG)
            .setCompressEngine(object : CompressFileEngine {
                override fun onStartCompress(
                    context: Context,
                    source: ArrayList<Uri>,
                    call: OnKeyValueResultCallbackListener?,
                ) {
                    Luban.with(context)
                        .load(source)
                        .ignoreBy(100)
                        .setCompressListener(object : OnNewCompressListener {
                            override fun onStart() {
                            }

                            override fun onSuccess(source: String, compressFile: File) {
                                call?.onCallback(source, compressFile.absolutePath)
                            }

                            override fun onError(source: String, e: Throwable) {
                                call?.onCallback(source, null)
                            }
                        })
                        .launch()
                }
            })
            .forResult(object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: ArrayList<LocalMedia>) {
                    val path = result.firstOrNull()?.compressPath ?: return
                    sendImageMessage(path)
                }

                override fun onCancel() {
                }
            })
    }

    override fun onResume() {
        super.onResume()
        viewModel.clearUnread(targetId)
    }

    fun sendImageMessage(path: String) {
        viewModel.sendImage(targetId, path, sp.userId)
    }

    override fun loadData(isPlaceholder: Boolean) {
        super.loadData(isPlaceholder)
        viewModel.loadInitial(targetId, Constant.DEFAULT_MESSAGE_COUNT)
    }

    fun loadMore() {
        viewModel.loadMore(targetId, Constant.DEFAULT_MESSAGE_COUNT)
    }

    fun sendTextMessage() {
        val content = inputContent.trim()
        if (StringUtils.isEmpty(content)) {
            SuperToast.show(R.string.hint_enter_message)
            return
        }

        viewModel.sendText(targetId, content, sp.userId)
    }

    fun clearInput(version: Long) {
        if (version == handledClearInputVersion) {
            return
        }

        handledClearInputVersion = version
        inputContent = ""
    }

    fun renderHistoryError(state: ChatUiState) {
        if (state.errorVersion == handledErrorVersion) {
            return
        }

        handledErrorVersion = state.errorVersion
        Timber.w("chat history error %s", state.errorCode)
    }

    fun renderSendError(state: ChatUiState) {
        if (state.sendErrorVersion == handledSendErrorVersion) {
            return
        }

        handledSendErrorVersion = state.sendErrorVersion
        if (state.sendError != null) {
            Timber.e(
                state.sendError,
                "chat send error %s %s",
                state.sendErrorMessage,
                state.sendErrorCode,
            )
        } else {
            Timber.e(
                "chat send error %s %s",
                state.sendErrorMessage,
                state.sendErrorCode,
            )
        }
    }

    fun renderUnreadClearError(state: ChatUiState) {
        if (state.unreadClearErrorVersion == handledUnreadClearErrorVersion) {
            return
        }

        handledUnreadClearErrorVersion = state.unreadClearErrorVersion
        Timber.w("chat clear unread error %s", state.unreadClearErrorCode)
    }
}
