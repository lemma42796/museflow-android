package com.ixuea.courses.mymusic.component.feed.activity

import android.content.Context
import android.net.Uri
import android.text.Editable
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.activity.BaseTitleActivity
import com.ixuea.courses.mymusic.adapter.TextWatcherAdapter
import com.ixuea.courses.mymusic.component.feed.adapter.ImageAdapter
import com.ixuea.courses.mymusic.component.feed.domain.CompressFeedImagesUseCase
import com.ixuea.courses.mymusic.component.feed.model.Feed
import com.ixuea.courses.mymusic.component.feed.ui.FeedPublishOperation
import com.ixuea.courses.mymusic.component.feed.ui.FeedPublishUiState
import com.ixuea.courses.mymusic.component.feed.ui.FeedPublishViewModel
import com.ixuea.courses.mymusic.config.glide.GlideEngine
import com.ixuea.courses.mymusic.databinding.ActivityPublishFeedBinding
import com.ixuea.superui.decoration.GridDividerItemDecoration
import com.ixuea.superui.toast.SuperToast
import com.ixuea.superui.util.DensityUtil
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.config.SelectModeConfig
import com.luck.picture.lib.engine.CompressFileEngine
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnKeyValueResultCallbackListener
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.ArrayList

/**
 * 发布动态界面
 */
class PublishFeedActivity : BaseTitleActivity<ActivityPublishFeedBinding>() {
    /**
     * 动态
     */
    private val feed = Feed()
    private val compressFeedImages = CompressFeedImagesUseCase()
    private lateinit var adapter: ImageAdapter
    private lateinit var viewModel: FeedPublishViewModel
    private var currentOperation = FeedPublishOperation.NONE
    private var handledRequestErrorVersion = 0L
    private var handledUploadCountErrorVersion = 0L
    private var handledPublishCompleteVersion = 0L

    override fun initViews() {
        super.initViews()
        binding.list.layoutManager = GridLayoutManager(hostActivity, 4)

        val itemDecoration = GridDividerItemDecoration(
            hostActivity,
            DensityUtil.dip2px(hostActivity, 5F).toInt()
        )
        binding.list.addItemDecoration(itemDecoration)
        binding.position.visibility = View.GONE
    }

    override fun initDatum() {
        super.initDatum()
        viewModel = ViewModelProvider(this)[FeedPublishViewModel::class.java]

        adapter = ImageAdapter(R.layout.item_image)
        binding.list.adapter = adapter

        observePublishState()
        viewModel.setSelectedImages(emptyList())
    }

    private fun setData(datum: List<Any>) {
        adapter.setNewInstance(ArrayList(datum))
    }

    /**
     * 返回菜单
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.publish, menu)
        menu.findItem(R.id.publish)?.isEnabled = currentOperation == FeedPublishOperation.NONE
        return true
    }

    /**
     * 按钮点击了
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.publish) {
            sendClick()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun initListeners() {
        super.initListeners()
        binding.content.addTextChangedListener(object : TextWatcherAdapter() {
            override fun afterTextChanged(s: Editable) {
                super.afterTextChanged(s)
                binding.count.text = getString(R.string.feed_count, s.toString().length)
            }
        })

        adapter.setOnItemClickListener { itemAdapter, _, position ->
            if (itemAdapter.getItem(position) is Int) {
                selectImage()
            }
        }

        adapter.addChildClickViewIds(R.id.close)
        adapter.setOnItemChildClickListener { _, _, position ->
            viewModel.removeSelectedImage(position)
        }
    }

    /**
     * 选择图片
     */
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

    private fun observePublishState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    render(state)
                }
            }
        }
    }

    private fun render(state: FeedPublishUiState) {
        setData(state.mediaItems)
        renderOperation(state.operation)

        if (state.requestErrorVersion != handledRequestErrorVersion) {
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

        if (state.uploadCountErrorVersion != handledUploadCountErrorVersion) {
            handledUploadCountErrorVersion = state.uploadCountErrorVersion
            SuperToast.show(R.string.error_upload_image)
        }

        if (state.publishCompleteVersion != handledPublishCompleteVersion) {
            handledPublishCompleteVersion = state.publishCompleteVersion
            finish()
        }
    }

    private fun renderOperation(operation: FeedPublishOperation) {
        if (currentOperation == operation) {
            return
        }

        currentOperation = operation
        invalidateOptionsMenu()
        when (operation) {
            FeedPublishOperation.NONE -> hideLoading()
            FeedPublishOperation.UPLOADING_IMAGES -> showLoading(getString(R.string.loading_upload, 1))
            FeedPublishOperation.CREATING_FEED -> showLoading(getString(R.string.loading))
        }
    }

    private fun sendClick() {
        val content = binding.content.text.toString().trim()

        if (content.isBlank()) {
            SuperToast.error(R.string.hint_feed)
            return
        }

        if (content.length > 140) {
            SuperToast.error(R.string.error_content_length)
            return
        }

        // 真实项目中应该由服务端判断发送设备，避免客户端破解后，可以设置任意值
        feed.content = "%s\n📱来自【Android Java云音乐客户端】".format(content)
        viewModel.publish(feed)
    }
}
