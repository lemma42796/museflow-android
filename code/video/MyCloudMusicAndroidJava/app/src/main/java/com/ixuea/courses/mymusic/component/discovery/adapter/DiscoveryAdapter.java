package com.ixuea.courses.mymusic.component.discovery.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.component.ad.model.Ad;
import com.ixuea.courses.mymusic.component.discovery.model.ui.BannerData;
import com.ixuea.courses.mymusic.component.discovery.model.ui.ButtonData;
import com.ixuea.courses.mymusic.component.discovery.model.ui.IconTitleButtonData;
import com.ixuea.courses.mymusic.component.discovery.model.ui.SheetData;
import com.ixuea.courses.mymusic.component.discovery.model.ui.SongData;
import com.ixuea.courses.mymusic.component.sheet.model.Sheet;
import com.ixuea.courses.mymusic.component.song.model.Song;
import com.ixuea.courses.mymusic.databinding.DiscoveryButtonBinding;
import com.ixuea.courses.mymusic.model.ui.BaseMultiItemEntity;
import com.ixuea.courses.mymusic.util.Constant;
import com.ixuea.courses.mymusic.util.ImageUtil;
import com.ixuea.courses.mymusic.util.ScreenUtil;
import com.ixuea.courses.mymusic.util.SuperDateUtil;
import com.ixuea.superui.decoration.GridDividerItemDecoration;
import com.ixuea.superui.util.DensityUtil;
import com.ixuea.superui.util.SuperRecyclerViewUtil;
import com.ixuea.superui.util.SuperViewUtil;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.CircleIndicator;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;

/**
 * 发现界面适配器
 */
public class DiscoveryAdapter extends BaseMultiItemQuickAdapter<BaseMultiItemEntity, BaseViewHolder> {

    private final Fragment fragment;
    private OnBannerListener onBannerListener;
    private SheetAdapter sheetAdapter;
    private DiscoveryAdapterListener discoveryAdapterListener;
    private DiscoverySongAdapter songAdapter;

    public DiscoveryAdapter(Fragment fragment, OnBannerListener onBannerListener) {
        super(new ArrayList<>());
        this.fragment = fragment;
        this.onBannerListener = onBannerListener;

        //添加多类型布局

        //banner类型
        addItemType(Constant.STYLE_BANNER, R.layout.item_discovery_banner);

        //快捷按钮类型
        addItemType(Constant.STYLE_BUTTON, R.layout.item_discovery_button);

        //添加歌单类型
        addItemType(Constant.STYLE_SHEET, R.layout.item_discovery_sheet);

        //添加单曲类型
        addItemType(Constant.STYLE_SONG, R.layout.item_discovery_sheet);

        //添加尾部类型
        addItemType(Constant.STYLE_FOOTER, R.layout.item_discovery_footer);
    }

    /**
     * 绑定数据方法
     * 复用等步骤不用管
     * 框架内部自动处理
     *
     * @param holder
     * @param d
     */
    @Override
    protected void convert(@NonNull BaseViewHolder holder, BaseMultiItemEntity d) {
        switch (holder.getItemViewType()) {
            case Constant.STYLE_BANNER:
                //banner
                BannerData data = (BannerData) d;

                Banner bannerView = holder.getView(R.id.banner);

                BannerImageAdapter<Ad> bannerImageAdapter = new BannerImageAdapter<Ad>(data.getData()) {

                    @Override
                    public void onBindView(BannerImageHolder holder, Ad data, int position, int size) {
                        ImageUtil.show(getContext(), (ImageView) holder.itemView, data.getIcon());
                    }
                };

                bannerView.setAdapter(bannerImageAdapter);

                bannerView.setOnBannerListener(onBannerListener);

                bannerView.setBannerRound(DensityUtil.dip2px(getContext(), 10));

                //添加生命周期观察者
                bannerView.addBannerLifecycleObserver(fragment);

                bannerView.setIndicator(new CircleIndicator(getContext()));
                break;
            case Constant.STYLE_BUTTON:
                //按钮
                bindButtonData(holder, (ButtonData) d);
                break;
            case Constant.STYLE_SHEET:
                //歌单
                bindSheetData(holder, (SheetData) d);
                break;
            case Constant.STYLE_SONG:
                //单曲
                bindSongData(holder, (SongData) d);
                break;
            case Constant.STYLE_FOOTER:
                holder.getView(R.id.refresh_button).setOnClickListener(v -> discoveryAdapterListener.onRefreshClick());
                holder.getView(R.id.custom).setOnClickListener(v -> discoveryAdapterListener.onCustomDiscoveryClick());
                break;
        }
    }

    private void bindSongData(BaseViewHolder holder, SongData data) {
        //设置标题，将标题放到每个具体的item上，好处是方便整体排序
        holder.setText(R.id.title, R.string.recommend_song);

        //显示更多容器
        holder.setVisible(R.id.more, true);
        holder.getView(R.id.more).setOnClickListener(v -> {
            if (discoveryAdapterListener != null) {
                discoveryAdapterListener.onSongMoreClick();
            }
        });

        RecyclerView listView = holder.getView(R.id.list);
        if (listView.getAdapter() == null) {
            SuperRecyclerViewUtil.initVerticalLinearRecyclerView(listView, false);

            songAdapter = new DiscoverySongAdapter(R.layout.item_discovery_song);

            //item点击
            songAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                    if (discoveryAdapterListener != null) {
                        discoveryAdapterListener.onSongClick((Song) adapter.getItem(position));
                    }
                }
            });

            listView.setAdapter(songAdapter);

        }

        songAdapter.setNewInstance(data.getData());
    }

    private void bindSheetData(BaseViewHolder holder, SheetData data) {
        //设置标题，将标题放到每个具体的item上，好处是方便整体排序
        holder.setText(R.id.title, R.string.recommend_sheet);

        //显示更多容器
        holder.setVisible(R.id.more, true);
        holder.getView(R.id.more).setOnClickListener(v -> {

        });

        RecyclerView listView = holder.getView(R.id.list);
        if (listView.getAdapter() == null) {
            //设置显示3列
            GridLayoutManager layoutManager = new GridLayoutManager(listView.getContext(), 3);
            listView.setLayoutManager(layoutManager);

            sheetAdapter = new SheetAdapter(R.layout.item_sheet);

            //item点击
            sheetAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                    if (discoveryAdapterListener != null) {
                        discoveryAdapterListener.onSheetClick((Sheet) adapter.getItem(position));
                    }
                }
            });
            listView.setAdapter(sheetAdapter);

            GridDividerItemDecoration itemDecoration = new GridDividerItemDecoration(getContext(), (int) DensityUtil.dip2px(getContext(), 5F));
            listView.addItemDecoration(itemDecoration);
        }

        sheetAdapter.setNewInstance(data.getData());
    }

    private void bindButtonData(BaseViewHolder holder, ButtonData data) {
        LinearLayout container = holder.getView(R.id.container);
        if (container.getChildCount() > 0) {
            //已经添加了
            return;
        }

        //横向显示5个半
        float containerWidth = ScreenUtil.getScreenWith(container.getContext()) - DensityUtil.dip2px(container.getContext(), 10 * 2);
        int itemWidth = (int) (containerWidth / 5.5);
        DiscoveryButtonBinding binding;
        LinearLayout.LayoutParams layoutParams;
        for (IconTitleButtonData it : data.getData()) {
            binding = DiscoveryButtonBinding.inflate(LayoutInflater.from(getContext()));
            binding.icon.setImageResource(it.getIcon());
            binding.title.setText(it.getTitle());

            if (it.getIcon() == R.drawable.day_recommend) {
                SuperViewUtil.show(binding.more);

                //显示日期
                binding.more.setText(String.valueOf(SuperDateUtil.currentDay()));
            }

            //设置点击事件
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            layoutParams = new LinearLayout.LayoutParams(itemWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
            container.addView(binding.getRoot(), layoutParams);
        }
    }

    public void setDiscoveryAdapterListener(DiscoveryAdapterListener discoveryAdapterListener) {
        this.discoveryAdapterListener = discoveryAdapterListener;
    }

    public interface DiscoveryAdapterListener {
        /**
         * 歌单点击
         *
         * @param data
         */
        void onSheetClick(Sheet data);

        /**
         * 歌单更多点击
         */
        void onSheetMoreClick();

        /**
         * 单曲更多点击
         */
        void onSongMoreClick();

        /**
         * 单曲点击
         *
         * @param data
         */
        void onSongClick(Song data);

        /**
         * 刷新点击
         */
        void onRefreshClick();

        /**
         * 自定义
         */
        void onCustomDiscoveryClick();
    }
}
