package com.ixuea.courses.mymusic.component.feed.fragment

import android.os.Bundle
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import autodispose2.AutoDispose.autoDisposable
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.component.api.HttpObserver
import com.ixuea.courses.mymusic.component.feed.activity.PublishFeedActivity
import com.ixuea.courses.mymusic.component.feed.adapter.FeedAdapter
import com.ixuea.courses.mymusic.component.feed.model.Feed
import com.ixuea.courses.mymusic.component.feed.model.event.FeedChangedEvent
import com.ixuea.courses.mymusic.component.feed.repository.FeedRepository
import com.ixuea.courses.mymusic.component.user.activity.UserDetailActivity
import com.ixuea.courses.mymusic.component.user.model.event.UserDetailEvent
import com.ixuea.courses.mymusic.databinding.FragmentFeedBinding
import com.ixuea.courses.mymusic.fragment.BaseViewModelFragment
import com.ixuea.courses.mymusic.model.response.ListResponse
import com.ixuea.courses.mymusic.util.Constant
import com.ixuea.courses.mymusic.util.ImageUtil
import com.wanglu.photoviewerlibrary.PhotoViewer
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 首页-动态界面
 */
class FeedFragment : BaseViewModelFragment<FragmentFeedBinding>(), FeedAdapter.FeedListener {
    private var userId: String? = null
    private lateinit var adapter: FeedAdapter
    private val repository = FeedRepository.getInstance()

    override fun isRegisterEventBus(): Boolean {
        return true
    }

    override fun initDatum() {
        super.initDatum()
        userId = arguments?.getString(Constant.USER_ID) ?: arguments?.getString(Constant.ID)

        adapter = FeedAdapter(R.layout.item_feed)
        binding.list.adapter = adapter

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
        repository.feeds(userId)
            .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
            .subscribe(object : HttpObserver<ListResponse<Feed>>() {
                override fun onSucceeded(data: ListResponse<Feed>) {
                    adapter.setNewInstance(data.data?.data?.toMutableList() ?: mutableListOf())
                }
            })
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
