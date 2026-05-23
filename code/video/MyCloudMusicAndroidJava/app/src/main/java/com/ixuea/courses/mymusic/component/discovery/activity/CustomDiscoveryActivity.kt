package com.ixuea.courses.mymusic.component.discovery.activity

import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.activity.BaseTitleActivity
import com.ixuea.courses.mymusic.component.discovery.adapter.CustomDiscoveryAdapter
import com.ixuea.courses.mymusic.component.discovery.domain.NotifyDiscoverySortChangedUseCase
import com.ixuea.courses.mymusic.component.discovery.model.ui.CustomDiscoveryItem
import com.ixuea.courses.mymusic.databinding.ActivityCustomDiscoveryBinding
import com.ixuea.courses.mymusic.util.Constant
import com.ixuea.superui.util.SuperRecyclerViewUtil
import timber.log.Timber
import java.util.Collections

/**
 * 自定义发现界面
 */
class CustomDiscoveryActivity : BaseTitleActivity<ActivityCustomDiscoveryBinding>() {
    private lateinit var adapter: CustomDiscoveryAdapter
    private lateinit var touchHelper: ItemTouchHelper
    private var useDefaultSort = false
    private val notifySortChanged = NotifyDiscoverySortChangedUseCase()

    override fun initViews() {
        super.initViews()
        SuperRecyclerViewUtil.initVerticalLinearRecyclerView(binding.list, false)
    }

    override fun initDatum() {
        super.initDatum()
        initListDrag()

        adapter = CustomDiscoveryAdapter(hostActivity, touchHelper)
        binding.list.adapter = adapter

        loadData()
    }

    override fun initListeners() {
        super.initListeners()
        binding.resetDefaultSort.setOnClickListener {
            useDefaultSort = true
            loadData()
        }
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
        adapter.setDatum(datum)
    }

    private fun createItem(style: Int, title: Int): CustomDiscoveryItem {
        return CustomDiscoveryItem(
            style,
            title,
            if (useDefaultSort) style else sp.getSort(style),
        )
    }

    private fun initListDrag() {
        touchHelper = ItemTouchHelper(
            object : ItemTouchHelper.Callback() {
                override fun getMovementFlags(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                ): Int {
                    return makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0)
                }

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder,
                ): Boolean {
                    if (viewHolder.itemViewType != target.itemViewType) {
                        return false
                    }

                    val sourcePosition = viewHolder.layoutPosition
                    val targetPosition = target.layoutPosition
                    Collections.swap(adapter.datum, sourcePosition, targetPosition)
                    adapter.notifyItemMoved(sourcePosition, targetPosition)
                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                }
            }
        )
        touchHelper.attachToRecyclerView(binding.list)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_save, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.save) {
            saveClick()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveClick() {
        Timber.d("saveClick")
        for (i in 0 until adapter.datum.size) {
            val data = adapter.getData(i)
            sp.setSort(data.style, i)
        }

        finish()
        notifySortChanged()
    }
}
