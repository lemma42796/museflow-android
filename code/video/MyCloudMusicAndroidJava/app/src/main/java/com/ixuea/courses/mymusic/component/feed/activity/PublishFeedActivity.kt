package com.ixuea.courses.mymusic.component.feed.activity

import android.content.Context
import android.net.Uri
import android.text.Editable
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import autodispose2.AutoDispose.autoDisposable
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.activity.BaseTitleActivity
import com.ixuea.courses.mymusic.adapter.TextWatcherAdapter
import com.ixuea.courses.mymusic.component.api.HttpObserver
import com.ixuea.courses.mymusic.component.feed.adapter.ImageAdapter
import com.ixuea.courses.mymusic.component.feed.model.Feed
import com.ixuea.courses.mymusic.component.feed.model.event.FeedChangedEvent
import com.ixuea.courses.mymusic.component.feed.repository.FeedPublishRepository
import com.ixuea.courses.mymusic.component.feed.repository.ImageCompressionRepository
import com.ixuea.courses.mymusic.component.feed.ui.FeedPublishViewModel
import com.ixuea.courses.mymusic.config.glide.GlideEngine
import com.ixuea.courses.mymusic.databinding.ActivityPublishFeedBinding
import com.ixuea.courses.mymusic.model.Base
import com.ixuea.courses.mymusic.model.Resource
import com.ixuea.courses.mymusic.model.response.DetailResponse
import com.ixuea.courses.mymusic.model.response.ListResponse
import com.ixuea.courses.mymusic.util.ImageCompressor
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
import org.greenrobot.eventbus.EventBus
import java.util.ArrayList

/**
 * 发布动态界面
 */
class PublishFeedActivity : BaseTitleActivity<ActivityPublishFeedBinding>() {
    private var content: String = ""

    /**
     * 动态
     */
    private val feed = Feed()
    private lateinit var adapter: ImageAdapter
    private lateinit var viewModel: FeedPublishViewModel
    private val publishRepository = FeedPublishRepository.getInstance()

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

        viewModel.mediaItems.observe(this, ::setData)
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
                    ImageCompressionRepository.getInstance().compressImages(
                        context,
                        source,
                        object : ImageCompressor.CompressionCallback {
                            override fun onCompressionComplete(
                                originalFilePath: String,
                                compressedFilePath: String
                            ) {
                                call.onCallback(originalFilePath, compressedFilePath)
                            }

                            override fun onCompressionError(e: Exception) {
                            }
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

    private fun sendClick() {
        content = binding.content.text.toString().trim()

        if (content.isBlank()) {
            SuperToast.error(R.string.hint_feed)
            return
        }

        if (content.length > 140) {
            SuperToast.error(R.string.error_content_length)
            return
        }

        val selectedImages = viewModel.getSelectedImages()
        if (selectedImages.isNotEmpty()) {
            uploadImages(selectedImages)
        } else {
            saveFeed(null)
        }
    }

    private fun uploadImages(datum: List<LocalMedia>) {
        showLoading(getString(R.string.loading_upload, 1))
        publishRepository.uploadImages(datum)
            .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
            .subscribe(object : HttpObserver<ListResponse<Resource>>() {
                override fun onFailed(data: ListResponse<Resource>?, e: Throwable?): Boolean {
                    hideLoading()
                    return super.onFailed(data, e)
                }

                override fun onSucceeded(data: ListResponse<Resource>) {
                    hideLoading()
                    val results = data.data?.data
                    if (results != null && results.size == datum.size) {
                        saveFeed(results)
                    } else {
                        SuperToast.show(R.string.error_upload_image)
                    }
                }
            })
    }

    private fun saveFeed(results: List<Resource>?) {
        // 真实项目中应该由服务端判断发送设备，避免客户端破解后，可以设置任意值
        feed.content = "%s\n📱来自【Android Java云音乐客户端】".format(content)
        feed.medias = results

        publishRepository.createFeed(feed)
            .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
            .subscribe(object : HttpObserver<DetailResponse<Base>>() {
                override fun onSucceeded(data: DetailResponse<Base>) {
                    EventBus.getDefault().post(FeedChangedEvent())
                    finish()
                }
            })
    }
}
