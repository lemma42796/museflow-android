package com.ixuea.courses.mymusic.component.discovery.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.activity.BaseLogicActivity
import com.ixuea.courses.mymusic.component.discovery.domain.NotifyDiscoverySortChangedUseCase
import com.ixuea.courses.mymusic.component.discovery.model.ui.CustomDiscoveryItem
import com.ixuea.courses.mymusic.component.discovery.ui.CustomDiscoveryScreen
import com.ixuea.courses.mymusic.ui.compose.MuseFlowTheme
import com.ixuea.courses.mymusic.util.Constant
import timber.log.Timber

/**
 * 自定义发现界面
 */
class CustomDiscoveryActivity : BaseLogicActivity() {
    private var items by mutableStateOf<List<CustomDiscoveryItem>>(emptyList())
    private var useDefaultSort = false
    private val notifySortChanged = NotifyDiscoverySortChangedUseCase()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MuseFlowTheme {
                CustomDiscoveryScreen(
                    items = items,
                    onBack = { onBackPressedDispatcher.onBackPressed() },
                    onSaveClick = ::saveClick,
                    onResetDefaultSortClick = {
                        useDefaultSort = true
                        loadData()
                    },
                    onMove = ::moveItem,
                )
            }
        }
    }

    override fun initDatum() {
        super.initDatum()
        loadData()
    }

    override fun loadData(isPlaceholder: Boolean) {
        super.loadData(isPlaceholder)
        val datum = mutableListOf(
            createItem(Constant.STYLE_BANNER, R.string.top_banner),
            createItem(Constant.STYLE_BUTTON, R.string.quick_button),
            createItem(Constant.STYLE_SHEET, R.string.recommend_sheet),
            createItem(Constant.STYLE_SONG, R.string.recommend_song),
        )

        datum.sortWith { first, second -> first.compareTo(second) }
        items = datum
    }

    private fun createItem(style: Int, title: Int): CustomDiscoveryItem {
        return CustomDiscoveryItem(
            style,
            title,
            if (useDefaultSort) style else sp.getSort(style),
        )
    }

    private fun moveItem(from: Int, to: Int) {
        if (from !in items.indices || to !in items.indices || from == to) {
            return
        }

        items = items.toMutableList().apply {
            add(to, removeAt(from))
        }
    }

    private fun saveClick() {
        Timber.d("saveClick")
        items.forEachIndexed { index, data ->
            sp.setSort(data.style, index)
        }

        finish()
        notifySortChanged()
    }
}
