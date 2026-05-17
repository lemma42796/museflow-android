package com.ixuea.courses.mymusic.component.discovery.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.component.ad.model.Ad
import com.ixuea.courses.mymusic.component.discovery.model.ui.BannerData
import com.ixuea.courses.mymusic.component.discovery.model.ui.BaseSort
import com.ixuea.courses.mymusic.component.discovery.model.ui.ButtonData
import com.ixuea.courses.mymusic.component.discovery.model.ui.SheetData
import com.ixuea.courses.mymusic.component.discovery.model.ui.SongData
import com.ixuea.courses.mymusic.component.sheet.model.Sheet
import com.ixuea.courses.mymusic.component.song.model.Song
import com.ixuea.courses.mymusic.databinding.DiscoveryButtonBinding
import com.ixuea.courses.mymusic.model.ui.BaseMultiItemEntity
import com.ixuea.courses.mymusic.util.Constant
import com.ixuea.courses.mymusic.util.ImageUtil
import com.ixuea.courses.mymusic.util.ScreenUtil
import com.ixuea.courses.mymusic.util.SuperDateUtil
import com.ixuea.superui.decoration.GridDividerItemDecoration
import com.ixuea.superui.util.DensityUtil
import com.ixuea.superui.util.SuperRecyclerViewUtil
import com.ixuea.superui.util.SuperViewUtil
import com.youth.banner.Banner
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder
import com.youth.banner.indicator.CircleIndicator
import com.youth.banner.listener.OnBannerListener

/**
 * 发现界面适配器
 */
class DiscoveryAdapter(
    private val fragment: Fragment,
    private val onBannerListener: OnBannerListener<Ad>,
) : BaseMultiItemQuickAdapter<BaseMultiItemEntity, BaseViewHolder>(arrayListOf()) {
    private var discoveryAdapterListener: DiscoveryAdapterListener? = null

    init {
        setHasStableIds(true)

        addItemType(Constant.STYLE_BANNER, R.layout.item_discovery_banner)
        addItemType(Constant.STYLE_BUTTON, R.layout.item_discovery_button)
        addItemType(Constant.STYLE_SHEET, R.layout.item_discovery_sheet)
        addItemType(Constant.STYLE_SONG, R.layout.item_discovery_sheet)
        addItemType(Constant.STYLE_FOOTER, R.layout.item_discovery_footer)
    }

    override fun getItemId(position: Int): Long {
        val item = getItem(position)
        if (item is BaseSort) {
            return 31L * item.itemType + item.sort
        }
        return item.itemType.toLong()
    }

    override fun convert(holder: BaseViewHolder, item: BaseMultiItemEntity) {
        when (holder.itemViewType) {
            Constant.STYLE_BANNER -> bindBannerData(holder, item as BannerData)
            Constant.STYLE_BUTTON -> bindButtonData(holder, item as ButtonData)
            Constant.STYLE_SHEET -> bindSheetData(holder, item as SheetData)
            Constant.STYLE_SONG -> bindSongData(holder, item as SongData)
            Constant.STYLE_FOOTER -> bindFooter(holder)
        }
    }

    private fun bindBannerData(holder: BaseViewHolder, data: BannerData) {
        val bannerView = holder.getView<Banner<Ad, BannerImageAdapter<Ad>>>(R.id.banner)
        val bannerImageAdapter = object : BannerImageAdapter<Ad>(data.data) {
            override fun onBindView(holder: BannerImageHolder, data: Ad, position: Int, size: Int) {
                ImageUtil.show(context, holder.itemView as ImageView, data.icon)
            }
        }

        bannerView.setAdapter(bannerImageAdapter)
        bannerView.setOnBannerListener(onBannerListener)
        bannerView.setBannerRound(DensityUtil.dip2px(context, 10F))
        bannerView.addBannerLifecycleObserver(fragment)
        bannerView.setIndicator(CircleIndicator(context))
    }

    private fun bindSongData(holder: BaseViewHolder, data: SongData) {
        holder.setText(R.id.title, R.string.recommend_song)
        holder.setVisible(R.id.more, true)
        holder.getView<android.view.View>(R.id.more).setOnClickListener {
            discoveryAdapterListener?.onSongMoreClick()
        }

        val listView = holder.getView<RecyclerView>(R.id.list)
        val adapter = (listView.adapter as? DiscoverySongAdapter) ?: DiscoverySongAdapter(
            R.layout.item_discovery_song
        ).also { newAdapter ->
            SuperRecyclerViewUtil.initVerticalLinearRecyclerView(listView, false)
            newAdapter.setOnItemClickListener { itemAdapter, _, position ->
                discoveryAdapterListener?.onSongClick(itemAdapter.getItem(position) as Song)
            }
            listView.adapter = newAdapter
        }

        adapter.setNewInstance(data.data)
    }

    private fun bindSheetData(holder: BaseViewHolder, data: SheetData) {
        holder.setText(R.id.title, R.string.recommend_sheet)
        holder.setVisible(R.id.more, true)
        holder.getView<android.view.View>(R.id.more).setOnClickListener {
        }

        val listView = holder.getView<RecyclerView>(R.id.list)
        val adapter = (listView.adapter as? SheetAdapter) ?: SheetAdapter(R.layout.item_sheet).also { newAdapter ->
            listView.layoutManager = GridLayoutManager(listView.context, 3)
            newAdapter.setOnItemClickListener { itemAdapter, _, position ->
                discoveryAdapterListener?.onSheetClick(itemAdapter.getItem(position) as Sheet)
            }
            listView.adapter = newAdapter

            val itemDecoration = GridDividerItemDecoration(
                context,
                DensityUtil.dip2px(context, 5F).toInt()
            )
            listView.addItemDecoration(itemDecoration)
        }

        adapter.setNewInstance(data.data)
    }

    private fun bindButtonData(holder: BaseViewHolder, data: ButtonData) {
        val container = holder.getView<LinearLayout>(R.id.container)
        if (container.childCount > 0) {
            return
        }

        val containerWidth = ScreenUtil.getScreenWith(container.context) -
            DensityUtil.dip2px(container.context, 10 * 2F)
        val itemWidth = (containerWidth / 5.5).toInt()
        data.data.forEach { item ->
            val binding = DiscoveryButtonBinding.inflate(LayoutInflater.from(context))
            binding.icon.setImageResource(item.icon)
            binding.title.setText(item.title)

            if (item.icon == R.drawable.day_recommend) {
                SuperViewUtil.show(binding.more)
                binding.more.text = SuperDateUtil.currentDay().toString()
            }

            binding.root.setOnClickListener {
            }

            val layoutParams = LinearLayout.LayoutParams(
                itemWidth,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            container.addView(binding.root, layoutParams)
        }
    }

    private fun bindFooter(holder: BaseViewHolder) {
        holder.getView<android.view.View>(R.id.refresh_button).setOnClickListener {
            discoveryAdapterListener?.onRefreshClick()
        }
        holder.getView<android.view.View>(R.id.custom).setOnClickListener {
            discoveryAdapterListener?.onCustomDiscoveryClick()
        }
    }

    fun setDiscoveryAdapterListener(discoveryAdapterListener: DiscoveryAdapterListener?) {
        this.discoveryAdapterListener = discoveryAdapterListener
    }

    interface DiscoveryAdapterListener {
        /**
         * 歌单点击
         */
        fun onSheetClick(data: Sheet)

        /**
         * 歌单更多点击
         */
        fun onSheetMoreClick()

        /**
         * 单曲更多点击
         */
        fun onSongMoreClick()

        /**
         * 单曲点击
         */
        fun onSongClick(data: Song)

        /**
         * 刷新点击
         */
        fun onRefreshClick()

        /**
         * 自定义
         */
        fun onCustomDiscoveryClick()
    }
}
