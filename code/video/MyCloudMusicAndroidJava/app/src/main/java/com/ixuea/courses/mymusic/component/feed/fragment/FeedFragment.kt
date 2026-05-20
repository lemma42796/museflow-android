package com.ixuea.courses.mymusic.component.feed.fragment

import android.os.Bundle
import android.widget.ImageView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.component.feed.activity.PublishFeedActivity
import com.ixuea.courses.mymusic.component.feed.adapter.FeedAdapter
import com.ixuea.courses.mymusic.component.feed.model.event.FeedChangedEvent
import com.ixuea.courses.mymusic.component.feed.ui.FeedUiState
import com.ixuea.courses.mymusic.component.feed.ui.FeedViewModel
import com.ixuea.courses.mymusic.component.user.activity.UserDetailActivity
import com.ixuea.courses.mymusic.component.user.model.event.UserDetailEvent
import com.ixuea.courses.mymusic.databinding.FragmentFeedBinding
import com.ixuea.courses.mymusic.fragment.BaseViewModelFragment
import com.ixuea.courses.mymusic.util.Constant
import com.ixuea.courses.mymusic.util.ImageUtil
import com.wanglu.photoviewerlibrary.PhotoViewer
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber

/**
 * 首页-动态界面
 */
class FeedFragment : BaseViewModelFragment<FragmentFeedBinding>(), FeedAdapter.FeedListener {
    private var userId: String? = null
    private lateinit var adapter: FeedAdapter
    private lateinit var viewModel: FeedViewModel
    private var handledDataVersion = 0L
    private var handledErrorVersion = 0L

    override fun isRegisterEventBus(): Boolean {
        return true
    }

    override fun initDatum() {
        super.initDatum()
        userId = arguments?.getString(Constant.USER_ID) ?: arguments?.getString(Constant.ID)
        viewModel = ViewModelProvider(this)[FeedViewModel::class.java]

        adapter = FeedAdapter(R.layout.item_feed)
        binding.list.adapter = adapter
        observeFeedState()

        loadData()
    }

    override fun initListeners() {
        super.initListeners()
        adapter.setListener(this)

        binding.primary.setOnClickListener {
            loginAfter {
                startActivity(PublishFeedActivity::class.java)
            }
        }
    }

    override fun loadData(isPlaceholder: Boolean) {
        viewModel.load(userId)
    }

    private fun observeFeedState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    render(state)
                }
            }
        }
    }

    private fun render(state: FeedUiState) {
        if (state.dataVersion != handledDataVersion) {
            handledDataVersion = state.dataVersion
            adapter.setNewInstance(state.feeds.toMutableList())
        }

        if (state.errorVersion != handledErrorVersion) {
            handledErrorVersion = state.errorVersion
            if (state.error != null) {
                Timber.e(state.error, "feed list error %s", state.errorMessage)
            } else {
                Timber.e("feed list error %s", state.errorMessage)
            }
        }
    }

    /**
     * 动态图片点击了
     */
    override fun onImageClick(rv: RecyclerView, results: List<String>, index: Int) {
        val imageUris = ArrayList(results)

        PhotoViewer
            .setData(imageUris)
            .setCurrentPage(index)
            .setImgContainer(rv)
            .setShowImageViewInterface(object : PhotoViewer.ShowImageViewInterface {
                override fun show(iv: ImageView, url: String) {
                    ImageUtil.show(hostActivity, iv, url)
                }
            })
            .start(this)
    }

    /**
     * 点赞，评论，里面的用户点击事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun userDetailEvent(event: UserDetailEvent) {
        UserDetailActivity.startWithId(hostActivity, event.data)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Suppress("UNUSED_PARAMETER")
    fun feedChangedEvent(event: FeedChangedEvent) {
        loadData()
    }

    companion object {
        @JvmStatic
        fun newInstance(): FeedFragment {
            return newInstance(null)
        }

        @JvmStatic
        fun newInstance(userId: String?): FeedFragment {
            return FeedFragment().apply {
                arguments = Bundle().apply {
                    if (!userId.isNullOrBlank()) {
                        putString(Constant.USER_ID, userId)
                    }
                }
            }
        }
    }
}
