package com.ixuea.courses.mymusic.component.discovery.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ixuea.courses.mymusic.component.discovery.activity.CustomDiscoveryActivity
import com.ixuea.courses.mymusic.component.discovery.domain.ObserveDiscoverySortChangesUseCase
import com.ixuea.courses.mymusic.component.discovery.ui.DiscoveryScreen
import com.ixuea.courses.mymusic.component.discovery.ui.DiscoveryUiState
import com.ixuea.courses.mymusic.component.discovery.ui.DiscoveryViewModel
import com.ixuea.courses.mymusic.component.sheet.activity.SheetDetailActivity
import com.ixuea.courses.mymusic.component.sheet.domain.ObserveSheetChangesUseCase
import com.ixuea.courses.mymusic.component.sheet.model.Sheet
import com.ixuea.courses.mymusic.component.song.model.Song
import com.ixuea.courses.mymusic.fragment.BaseLogicFragment
import com.ixuea.courses.mymusic.ui.compose.MuseFlowTheme
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * 首页-发现界面
 */
class DiscoveryFragment : BaseLogicFragment() {
    private lateinit var composeView: ComposeView
    private lateinit var viewModel: DiscoveryViewModel
    private var handledErrorVersion = 0L
    private val observeDiscoverySortChanges = ObserveDiscoverySortChangesUseCase()
    private val observeSheetChanges = ObserveSheetChangesUseCase()

    override fun getLayoutView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        composeView = ComposeView(inflater.context).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed,
            )
        }
        return composeView
    }

    override fun initDatum() {
        super.initDatum()
        viewModel = ViewModelProvider(this)[DiscoveryViewModel::class.java]
        renderContent()
        observeDiscoveryEvents()
        loadData()
    }

    override fun loadData(isPlaceholder: Boolean) {
        super.loadData(isPlaceholder)
        viewModel.load(sp)
    }

    private fun renderContent() {
        composeView.setContent {
            val state by viewModel.uiState.collectAsState()

            LaunchedEffect(state.errorVersion) {
                renderError(state)
            }

            MuseFlowTheme {
                DiscoveryScreen(
                    state = state,
                    lifecycleOwner = this@DiscoveryFragment,
                    onSheetClick = ::onSheetClick,
                    onSongClick = ::onSongClick,
                    onRefreshClick = ::onRefreshClick,
                    onCustomDiscoveryClick = ::onCustomDiscoveryClick,
                )
            }
        }
    }

    private fun observeDiscoveryEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    observeDiscoverySortChanges().collect {
                        onRefreshClick()
                    }
                }

                launch {
                    observeSheetChanges().collect {
                        loadData()
                    }
                }
            }
        }
    }

    private fun renderError(state: DiscoveryUiState) {
        if (state.errorVersion == handledErrorVersion) {
            return
        }

        handledErrorVersion = state.errorVersion
        Timber.e(state.error, "discovery page error")
    }

    private fun onSheetClick(data: Sheet) {
        startActivityExtraId(SheetDetailActivity::class.java, data.id)
    }

    private fun onSongClick(data: Song) {
        musicListManager.datum = listOf(data)
        musicListManager.play(data)
        startMusicPlayerActivity()
    }

    private fun onRefreshClick() {
        loadData()
    }

    private fun onCustomDiscoveryClick() {
        startActivity(CustomDiscoveryActivity::class.java)
    }

    companion object {
        @JvmStatic
        fun newInstance(): DiscoveryFragment {
            return DiscoveryFragment().apply {
                arguments = Bundle()
            }
        }
    }
}
