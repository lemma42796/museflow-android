package com.ixuea.courses.mymusic.component.discovery.fragment;

import static autodispose2.AutoDispose.autoDisposable;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.ixuea.courses.mymusic.MainActivity;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.activity.BaseLogicActivity;
import com.ixuea.courses.mymusic.component.ad.model.Ad;
import com.ixuea.courses.mymusic.component.api.HttpObserver;
import com.ixuea.courses.mymusic.component.discovery.activity.CustomDiscoveryActivity;
import com.ixuea.courses.mymusic.component.discovery.adapter.DiscoveryAdapter;
import com.ixuea.courses.mymusic.component.discovery.model.event.SortChangedEvent;
import com.ixuea.courses.mymusic.component.discovery.model.ui.BannerData;
import com.ixuea.courses.mymusic.component.discovery.model.ui.BaseSort;
import com.ixuea.courses.mymusic.component.discovery.model.ui.ButtonData;
import com.ixuea.courses.mymusic.component.discovery.model.ui.FooterData;
import com.ixuea.courses.mymusic.component.discovery.model.ui.SheetData;
import com.ixuea.courses.mymusic.component.discovery.model.ui.SongData;
import com.ixuea.courses.mymusic.component.search.model.SearchHistory;
import com.ixuea.courses.mymusic.component.sheet.activity.SheetDetailActivity;
import com.ixuea.courses.mymusic.component.sheet.model.Sheet;
import com.ixuea.courses.mymusic.component.sheet.model.event.SheetChangedEvent;
import com.ixuea.courses.mymusic.component.song.model.Song;
import com.ixuea.courses.mymusic.databinding.FragmentDiscoveryBinding;
import com.ixuea.courses.mymusic.fragment.BaseViewModelFragment;
import com.ixuea.courses.mymusic.model.response.ListResponse;
import com.ixuea.courses.mymusic.model.ui.BaseMultiItemEntity;
import com.ixuea.courses.mymusic.repository.DefaultRepository;
import com.ixuea.courses.mymusic.service.MusicPlayerService;
import com.ixuea.courses.mymusic.util.Constant;
import com.ixuea.courses.mymusic.util.FileUtil;
import com.ixuea.courses.mymusic.util.LiteORMUtil;
import com.ixuea.courses.mymusic.util.ResourceUtil;
import com.ixuea.superui.util.SuperDelayUtil;
import com.youth.banner.listener.OnBannerListener;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    private List<BaseMultiItemEntity> datum;
    private LinearLayoutManager layoutManager;
    private DiscoveryAdapter adapter;
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
        //创建适配器
        adapter = new DiscoveryAdapter(this, this);
        adapter.setDiscoveryAdapterListener(this);

        //设置适配器
        binding.list.setAdapter(adapter);

        loadData();

        //测试数据库
        LiteORMUtil orm = LiteORMUtil.getInstance(getHostActivity());

        //创建对象
        SearchHistory searchHistory = new SearchHistory();

        //赋值
        searchHistory.setContent("我们是爱学啊");
        searchHistory.setCreatedAt(System.currentTimeMillis());

        orm.createOrUpdate(searchHistory);

        searchHistory = new SearchHistory();

        //赋值
        searchHistory.setContent("人生苦短");
        searchHistory.setCreatedAt(System.currentTimeMillis());

        orm.createOrUpdate(searchHistory);

        //查询所有
        List<SearchHistory> results = orm.querySearchHistory();
        Log.d(TAG, "querySearchHistory: " + results.size());
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

        datum = new ArrayList<>();

        //广告API
        Observable<ListResponse<Ad>> ads = DefaultRepository.getInstance().bannerAd();

        ads
                .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new HttpObserver<ListResponse<Ad>>(this) {
                    @Override
                    public boolean onFailed(ListResponse<Ad> data, Throwable e) {
                        endRefresh();
                        return super.onFailed(data, e);
                    }

                    @Override
                    public void onSucceeded(ListResponse<Ad> data) {
                        //添加轮播图
                        datum.add(new BannerData(
                                data.getData().getData(),
                                sp.getSort(Constant.STYLE_BANNER)
                        ));

                        //添加快捷按钮
                        datum.add(new ButtonData(sp.getSort(Constant.STYLE_BUTTON)));

                        //请求歌单数据
                        loadSheetData();
                    }
                });
    }

    private void loadSheetData() {
        //歌单API
        Observable<ListResponse<Sheet>> api = DefaultRepository.getInstance().sheets(Constant.SIZE12);

        api.to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new HttpObserver<ListResponse<Sheet>>(this) {
                    @Override
                    public boolean onFailed(ListResponse<Sheet> data, Throwable e) {
                        endRefresh();
                        return super.onFailed(data, e);
                    }

                    @Override
                    public void onSucceeded(ListResponse<Sheet> data) {
                        //添加歌单数据
                        datum.add(new SheetData(data.getData().getData(), sp.getSort(Constant.STYLE_SHEET)));

                        loadSongData();
                    }
                });
    }

    private void loadSongData() {
        Observable<ListResponse<Song>> api = DefaultRepository.getInstance().songs();

        api.to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new HttpObserver<ListResponse<Song>>(this) {
                    @Override
                    public boolean onFailed(ListResponse<Song> data, Throwable e) {
                        endRefresh();
                        return super.onFailed(data, e);
                    }

                    @Override
                    public void onSucceeded(ListResponse<Song> data) {
                        datum.add(new SongData(data.getData().getData(), sp.getSort(Constant.STYLE_SONG)));

                        //添加尾部数据
                        datum.add(new FooterData());

                        //结束时间
                        long endTime = System.currentTimeMillis();

                        //网络请求消耗的时间
                        long consumeTime = endTime - startTime;

                        if (consumeTime < 1000) {
                            //小于1秒钟，要延迟
                            SuperDelayUtil.delay(1000 - consumeTime, () -> show());
                        } else {
                            show();
                        }

                        loadSplashAd();
                    }
                });

    }

    private void loadSplashAd() {
        DefaultRepository.getInstance().splashAd()
                .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new HttpObserver<ListResponse<Ad>>() {
                    @Override
                    public void onSucceeded(ListResponse<Ad> data) {
                        List<Ad> results = data.getData().getData();
                        if (CollectionUtils.isNotEmpty(results)) {
                            downloadAd(results.get(0));
                        } else {
                            //删除本地广告数据
                            deleteSplashAd();
                        }
                    }
                });
    }

    private void downloadAd(Ad data) {
//        if (SuperNetworkUtil.isWifiConnected(getHostActivity())) {
        //wifi才下载
        sp.setSplashAd(data);

        //判断文件是否存在，如果存在就不下载
        File targetFile = FileUtil.adFile(getHostActivity(), data.getIcon());
        if (targetFile.exists()) {
            return;
        }

        new Thread(
                new Runnable() {
                    @Override
                    public void run() {

                        try {
                            //FutureTarget会阻塞
                            //所以需要在子线程调用
                            FutureTarget<File> target = Glide.with(getHostActivity().getApplicationContext())
                                    .asFile()
                                    .load(ResourceUtil.resourceUri(data.getIcon()))
                                    .submit();

                            //获取下载的文件
                            File file = target.get();

                            //将文件拷贝到我们需要的位置
                            FileUtils.moveFile(file, targetFile);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).start();
//        }
    }

    /**
     * 删除启动界面广告
     */
    private void deleteSplashAd() {
        //获取广告信息
        Ad ad = sp.getSplashAd();
        if (ad != null) {
            //删除配置文件
            sp.setSplashAd(null);

            //删除文件
            FileUtils.deleteQuietly(FileUtil.adFile(getHostActivity(), ad.getIcon()));
        }
    }

    private void show() {
        endRefresh();

        //排序
        Collections.sort(datum, (o1, o2) -> {
            BaseSort d1 = (BaseSort) o1;
            BaseSort d2 = (BaseSort) o2;
            return d1.compareTo(d2);
        });

        adapter.setNewInstance(datum);
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
        ((MainActivity) getHostActivity()).processAdClick((Ad) data);
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
