package com.ixuea.courses.mymusic.component.chat.activity

import android.content.Context
import android.net.Uri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.activity.BaseTitleActivity
import com.ixuea.courses.mymusic.component.chat.adapter.ChatAdapter
import com.ixuea.courses.mymusic.component.chat.ui.ChatSendOperation
import com.ixuea.courses.mymusic.component.chat.ui.ChatUiState
import com.ixuea.courses.mymusic.component.chat.ui.ChatViewModel
import com.ixuea.courses.mymusic.config.glide.GlideEngine
import com.ixuea.courses.mymusic.databinding.ActivityChatBinding
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
import kotlinx.coroutines.launch
import org.apache.commons.lang3.StringUtils
import timber.log.Timber
import top.zibin.luban.Luban
import top.zibin.luban.OnNewCompressListener

/**
 * 聊天界面
 */
class ChatActivity : BaseTitleActivity<ActivityChatBinding>() {
    private var targetId: String = ""
    private lateinit var adapter: ChatAdapter
    private lateinit var viewModel: ChatViewModel
    private var handledTargetTitleVersion = 0L
    private var handledDataVersion = 0L
    private var handledErrorVersion = 0L
    private var handledSendErrorVersion = 0L
    private var handledUnreadClearErrorVersion = 0L
    private var handledScrollToBottomVersion = 0L
    private var handledSmoothScrollBottomVersion = 0L
    private var handledClearInputVersion = 0L

    override fun initViews() {
        super.initViews()
        binding.refresh.setColorSchemeResources(R.color.primary)
        binding.refresh.setProgressBackgroundColorSchemeResource(R.color.white)
    }

    override fun initDatum() {
        super.initDatum()

        targetId = extraId().orEmpty()
        viewModel = ViewModelProvider(this)[ChatViewModel::class.java]
        viewModel.observeIncomingMessages(targetId)

        adapter = ChatAdapter(hostActivity)
        binding.list.adapter = adapter
        observeChatState()

        loadData()
    }

    override fun initListeners() {
        super.initListeners()
        binding.refresh.setOnRefreshListener { loadMore() }
        binding.selectImage.setOnClickListener { selectImage() }
        binding.send.setOnClickListener { sendTextMessage() }
    }

    private fun selectImage() {
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
                    call: OnKeyValueResultCallbackListener?
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

    private fun sendImageMessage(path: String) {
        viewModel.sendImage(targetId, path, sp.userId)
    }

    override fun loadData(isPlaceholder: Boolean) {
        super.loadData(isPlaceholder)

        viewModel.loadInitial(targetId, Constant.DEFAULT_MESSAGE_COUNT)
    }

    private fun loadMore() {
        viewModel.loadMore(targetId, Constant.DEFAULT_MESSAGE_COUNT)
    }

    private fun observeChatState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    render(state)
                }
            }
        }
    }

    private fun render(state: ChatUiState) {
        if (state.targetTitleVersion != handledTargetTitleVersion) {
            handledTargetTitleVersion = state.targetTitleVersion
            title = state.targetTitle
        }

        if (!state.isLoadingHistory) {
            binding.refresh.isRefreshing = false
        }

        if (state.errorVersion != handledErrorVersion) {
            handledErrorVersion = state.errorVersion
            Timber.w("chat history error %s", state.errorCode)
        }

        if (state.sendErrorVersion != handledSendErrorVersion) {
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

        if (state.clearInputVersion != handledClearInputVersion) {
            handledClearInputVersion = state.clearInputVersion
            clearInput()
        }

        if (state.unreadClearErrorVersion != handledUnreadClearErrorVersion) {
            handledUnreadClearErrorVersion = state.unreadClearErrorVersion
            Timber.w("chat clear unread error %s", state.unreadClearErrorCode)
        }

        renderSendState(state)

        if (state.dataVersion != handledDataVersion) {
            handledDataVersion = state.dataVersion
            adapter.setDatum(state.messages)
        }

        if (state.scrollToBottomVersion != handledScrollToBottomVersion) {
            handledScrollToBottomVersion = state.scrollToBottomVersion
            scrollBottom()
        }

        if (state.smoothScrollBottomVersion != handledSmoothScrollBottomVersion) {
            handledSmoothScrollBottomVersion = state.smoothScrollBottomVersion
            smoothScrollBottom()
        }
    }

    private fun sendTextMessage() {
        val content = binding.input.text.toString().trim()
        if (StringUtils.isEmpty(content)) {
            SuperToast.show(R.string.hint_enter_message)
            return
        }

        viewModel.sendText(targetId, content, sp.userId)
    }

    private fun clearInput() {
        binding.input.setText("")
    }

    private fun renderSendState(state: ChatUiState) {
        val isSending = state.sendOperation != ChatSendOperation.NONE
        binding.send.isEnabled = !isSending
        binding.selectImage.isEnabled = !isSending
    }

    private fun scrollBottom() {
        if (adapter.itemCount == 0) {
            return
        }

        binding.list.post {
            binding.list.scrollToPosition(adapter.itemCount - 1)
        }
    }

    private fun smoothScrollBottom() {
        if (adapter.itemCount == 0) {
            return
        }

        binding.list.post {
            binding.list.smoothScrollToPosition(adapter.itemCount - 1)
        }
    }
}
