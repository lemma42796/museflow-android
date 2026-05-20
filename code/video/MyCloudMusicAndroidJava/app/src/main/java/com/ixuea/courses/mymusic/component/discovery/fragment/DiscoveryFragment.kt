package com.ixuea.courses.mymusic.component.discovery.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.activity.BaseLogicActivity
import com.ixuea.courses.mymusic.component.ad.model.Ad
import com.ixuea.courses.mymusic.component.discovery.activity.CustomDiscoveryActivity
import com.ixuea.courses.mymusic.component.discovery.adapter.DiscoveryAdapter
import com.ixuea.courses.mymusic.component.discovery.model.event.SortChangedEvent
import com.ixuea.courses.mymusic.component.discovery.ui.DiscoveryUiState
import com.ixuea.courses.mymusic.component.discovery.ui.DiscoveryViewModel
import com.ixuea.courses.mymusic.component.sheet.activity.SheetDetailActivity
import com.ixuea.courses.mymusic.component.sheet.model.Sheet
import com.ixuea.courses.mymusic.component.sheet.model.event.SheetChangedEvent
import com.ixuea.courses.mymusic.component.song.model.Song
import com.ixuea.courses.mymusic.databinding.FragmentDiscoveryBinding
import com.ixuea.courses.mymusic.fragment.BaseViewModelFragment
import com.ixuea.courses.mymusic.model.ui.BaseMultiItemEntity
import com.ixuea.courses.mymusic.service.MusicPlayerService
import com.ixuea.superui.util.SuperDelayUtil
import com.youth.banner.listener.OnBannerListener
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber

/**
 * 首页-发现界面
 */
class DiscoveryFragment :
    BaseViewModelFragment<FragmentDiscoveryBinding>(),
    OnBannerListener<Ad>,
    DiscoveryAdapter.DiscoveryAdapterListener {

    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapter: DiscoveryAdapter
    private lateinit var viewModel: DiscoveryViewModel
    private var startTime: Long = 0
    private var handledDataVersion = 0L
    private var handledErrorVersion = 0L

    override fun isRegisterEventBus(): Boolean {
        return true
    }

    override fun initViews() {
        super.initViews()
        binding.list.setHasFixedSize(true)

        layoutManager = LinearLayoutManager(hostActivity)
        binding.list.layoutManager = layoutManager

        val decoration = DividerItemDecoration(binding.list.context, RecyclerView.VERTICAL)
        binding.list.addItemDecoration(decoration)

        val density = resources.displayMetrics.density
        Log.d(TAG, "initViews: $density")

        binding.refresh.setColorSchemeResources(R.color.primary)
        binding.refresh.setProgressBackgroundColorSchemeResource(R.color.white)
    }

    override fun initDatum() {
        super.initDatum()
        viewModel = ViewModelProvider(this)[DiscoveryViewModel::class.java]

        adapter = DiscoveryAdapter(this, this)
        adapter.setDiscoveryAdapterListener(this)
        binding.list.adapter = adapter
        observeDiscoveryState()

        loadData()
    }

    override fun initListeners() {
        super.initListeners()
        binding.refresh.setOnRefreshListener { loadData() }
    }

    private fun endRefresh() {
        binding.refresh.isRefreshing = false
    }

    override fun loadData(isPlaceholder: Boolean) {
        super.loadData(isPlaceholder)

        startTime = System.currentTimeMillis()
        binding.refresh.isRefreshing = true

        viewModel.load(sp)
    }

    private fun observeDiscoveryState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    render(state)
                }
            }
        }
    }

    private fun render(state: DiscoveryUiState) {
        if (state.dataVersion != handledDataVersion) {
            handledDataVersion = state.dataVersion
            val consumeTime = System.currentTimeMillis() - startTime

            if (consumeTime < MIN_REFRESH_DURATION_MS) {
                SuperDelayUtil.delay(MIN_REFRESH_DURATION_MS - consumeTime) {
                    show(state.sections)
                }
            } else {
                show(state.sections)
            }
        }

        if (state.errorVersion != handledErrorVersion) {
            handledErrorVersion = state.errorVersion
            endRefresh()
            Timber.e(state.error, "discovery page error")
        }
    }

    private fun show(data: List<BaseMultiItemEntity>) {
        endRefresh()
        adapter.setNewInstance(data.toMutableList())
    }

    /**
     * 轮播图点击
     */
    @Suppress("UNUSED_PARAMETER")
    override fun OnBannerClick(data: Ad, position: Int) {
        // Ads are not part of the public slim feature set.
    }

    override fun onSheetClick(data: Sheet) {
        Log.d(TAG, "onSheetClick: ${data.title}")
        startActivityExtraId(SheetDetailActivity::class.java, data.id)
    }

    override fun onSheetMoreClick() {
    }

    override fun onSongMoreClick() {
        Log.d(TAG, "onSongMoreClick")
        val intent = Intent(hostActivity, MusicPlayerService::class.java)
        hostActivity.startService(intent)
    }

    override fun onSongClick(data: Song) {
        Log.d(TAG, "onSongClick: ${data.title}")

        musicListManager.datum = listOf(data)
        musicListManager.play(data)

        (hostActivity as BaseLogicActivity).startMusicPlayerActivity()
    }

    override fun onRefreshClick() {
        binding.list.smoothScrollToPosition(0)
        binding.list.postDelayed({ loadData() }, 200)
    }

    override fun onCustomDiscoveryClick() {
        startActivity(CustomDiscoveryActivity::class.java)
    }

    /**
     * 排序改变了事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    @Suppress("UNUSED_PARAMETER")
    fun sortChangeEvent(event: SortChangedEvent) {
        onRefreshClick()
    }

    /**
     * 歌单改变了事件
     *
     * 例如：在歌单详情，收藏或取消了收藏
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    @Suppress("UNUSED_PARAMETER")
    fun sheetChangedEvent(event: SheetChangedEvent) {
        loadData()
    }

    companion object {
        private const val TAG = "DiscoveryFragment"
        private const val MIN_REFRESH_DURATION_MS = 1000L

        @JvmStatic
        fun newInstance(): DiscoveryFragment {
            return DiscoveryFragment().apply {
                arguments = Bundle()
            }
        }
    }
}
