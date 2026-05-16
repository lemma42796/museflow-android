package com.ixuea.courses.mymusic.component.discovery.fragment;

import static autodispose2.AutoDispose.autoDisposable;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.activity.BaseLogicActivity;
import com.ixuea.courses.mymusic.component.api.HttpObserver;
import com.ixuea.courses.mymusic.component.discovery.activity.CustomDiscoveryActivity;
import com.ixuea.courses.mymusic.component.discovery.adapter.DiscoveryAdapter;
import com.ixuea.courses.mymusic.component.discovery.model.DiscoveryPage;
import com.ixuea.courses.mymusic.component.discovery.model.event.SortChangedEvent;
import com.ixuea.courses.mymusic.component.discovery.repository.DiscoveryRepository;
import com.ixuea.courses.mymusic.component.sheet.activity.SheetDetailActivity;
import com.ixuea.courses.mymusic.component.sheet.model.Sheet;
import com.ixuea.courses.mymusic.component.sheet.model.event.SheetChangedEvent;
import com.ixuea.courses.mymusic.component.song.model.Song;
import com.ixuea.courses.mymusic.databinding.FragmentDiscoveryBinding;
import com.ixuea.courses.mymusic.fragment.BaseViewModelFragment;
import com.ixuea.courses.mymusic.model.ui.BaseMultiItemEntity;
import com.ixuea.courses.mymusic.service.MusicPlayerService;
import com.ixuea.superui.util.SuperDelayUtil;
import com.youth.banner.listener.OnBannerListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.List;

import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;
import io.reactivex.rxjava3.core.Observable;

/**
 * 首页-发现界面
 */
public class DiscoveryFragment extends BaseViewModelFragment<FragmentDiscoveryBinding> implements OnBannerListener, DiscoveryAdapter.DiscoveryAdapterListener {
    private static final String TAG = "DiscoveryFragment";

    /**
     * 列表数据集合
     */
    private LinearLayoutManager layoutManager;
    private DiscoveryAdapter adapter;
    private DiscoveryRepository discoveryRepository;
    private long startTime;

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    @Override
    protected void initViews() {
        super.initViews();
        //高度固定
        //可以提交性能
        //但由于这里是项目课程
        //所以这里不讲解
        //会在《详解RecyclerView》课程中讲解
        //http://www.ixuea.com/courses/8
        binding.list.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getHostActivity());
        binding.list.setLayoutManager(layoutManager);

        //分割线
        DividerItemDecoration decoration = new DividerItemDecoration(binding.list.getContext(), RecyclerView.VERTICAL);
        binding.list.addItemDecoration(decoration);

        float density = getResources().getDisplayMetrics().density;
        Log.d(TAG, "initViews: " + density);

        //刷新箭头颜色
        binding.refresh.setColorSchemeResources(R.color.primary);

        //刷新圆圈颜色
        binding.refresh.setProgressBackgroundColorSchemeResource(R.color.white);
    }

    @Override
    protected void initDatum() {
        super.initDatum();
        discoveryRepository = DiscoveryRepository.getInstance();

        //创建适配器
        adapter = new DiscoveryAdapter(this, this);
        adapter.setDiscoveryAdapterListener(this);

        //设置适配器
        binding.list.setAdapter(adapter);

        loadData();
    }

    @Override
    protected void initListeners() {
        super.initListeners();
//        binding.refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                loadData();
//            }
//        });

        binding.refresh.setOnRefreshListener(() -> loadData());
    }

    private void endRefresh() {
        binding.refresh.setRefreshing(false);
    }

    @Override
    protected void loadData(boolean isPlaceholder) {
        super.loadData(isPlaceholder);

        //记录开始时间，目的是始终要当前界面最低延迟1秒在显示内容
        //这样刷新效果才不至于一瞬间就没有了
        startTime = System.currentTimeMillis();

        binding.refresh.setRefreshing(true);

        discoveryRepository.homeSections(sp)
                .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new HttpObserver<DiscoveryPage>(this) {
                    @Override
                    public boolean onFailed(DiscoveryPage data, Throwable e) {
                        endRefresh();
                        return super.onFailed(data, e);
                    }

                    @Override
                    public void onSucceeded(DiscoveryPage data) {
                        //结束时间
                        long endTime = System.currentTimeMillis();

                        //网络请求消耗的时间
                        long consumeTime = endTime - startTime;

                        if (consumeTime < 1000) {
                            //小于1秒钟，要延迟
                            SuperDelayUtil.delay(1000 - consumeTime, () -> show(data.getSections()));
                        } else {
                            show(data.getSections());
                        }

                    }
                });

    }

    private void show(List<BaseMultiItemEntity> data) {
        endRefresh();
        adapter.setNewInstance(data);
    }

    public static DiscoveryFragment newInstance() {

        Bundle args = new Bundle();

        DiscoveryFragment fragment = new DiscoveryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * 轮播图点击
     *
     * @param data
     * @param position
     */
    @Override
    public void OnBannerClick(Object data, int position) {
        // Ads are not part of the public slim feature set.
    }

    @Override
    public void onSheetClick(Sheet data) {
        Log.d(TAG, "onSheetClick: " + data.getTitle());
        startActivityExtraId(SheetDetailActivity.class, data.getId());
    }

    @Override
    public void onSheetMoreClick() {

    }

    @Override
    public void onSongMoreClick() {
        Log.d(TAG, "onSongMoreClick");
//        NotificationUtil.showAlert(R.string.error_message_login);
        Intent intent = new Intent(getHostActivity(), MusicPlayerService.class);
        getHostActivity().startService(intent);
    }

    @Override
    public void onSongClick(Song data) {
        Log.d(TAG, "onSongClick: " + data.getTitle());

        getMusicListManager().setDatum(Arrays.asList(data));

        getMusicListManager().play(data);

        ((BaseLogicActivity) getHostActivity()).startMusicPlayerActivity();
    }

    @Override
    public void onRefreshClick() {
        binding.list.smoothScrollToPosition(0);

        //延时200毫秒，执行加载数据，目的是让列表先向上滚动到顶部
        binding.list.postDelayed(() -> loadData(), 200);
    }

    @Override
    public void onCustomDiscoveryClick() {
        startActivity(CustomDiscoveryActivity.class);
    }

    /**
     * 排序改变了事件
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void sortChangeEvent(SortChangedEvent event) {
        onRefreshClick();
    }

    /**
     * 歌单改变了事件
     * <p>
     * 例如：在歌单详情，收藏或取消了收藏
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void sheetChangedEvent(SheetChangedEvent event) {
        loadData();
    }
}
