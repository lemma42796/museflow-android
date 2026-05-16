package com.ixuea.courses.mymusic.component.video.activity;

import static autodispose2.AutoDispose.autoDisposable;

import android.content.res.Configuration;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.google.android.material.chip.Chip;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.activity.BaseTitleActivity;
import com.ixuea.courses.mymusic.component.api.HttpObserver;
import com.ixuea.courses.mymusic.component.comment.model.Comment;
import com.ixuea.courses.mymusic.component.video.adapter.VideoDetailAdapter;
import com.ixuea.courses.mymusic.component.video.model.Video;
import com.ixuea.courses.mymusic.databinding.ActivityVideoDetailBinding;
import com.ixuea.courses.mymusic.databinding.HeaderVideoDetailBinding;
import com.ixuea.courses.mymusic.model.response.DetailResponse;
import com.ixuea.courses.mymusic.model.response.ListResponse;
import com.ixuea.courses.mymusic.repository.DefaultRepository;
import com.ixuea.courses.mymusic.util.DefaultPreferenceUtil;
import com.ixuea.courses.mymusic.util.ImageUtil;
import com.ixuea.courses.mymusic.util.ResourceUtil;
import com.ixuea.courses.mymusic.util.SuperDateUtil;
import com.ixuea.superui.dialog.SuperDialog;
import com.ixuea.superui.util.SuperNetworkUtil;
import com.ixuea.superui.util.SuperStatusBarUtil;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;
import timber.log.Timber;

/**
 * 视频详情
 */
public class VideoDetailActivity extends BaseTitleActivity<ActivityVideoDetailBinding> {

    private String id;
    private Video data;
    private int seek;
    private OrientationUtils orientationUtils;
    private boolean isPlay;
    private boolean isPause;
    private VideoDetailAdapter adapter;
    private LRecyclerViewAdapter adapterWrapper;
    private HeaderVideoDetailBinding headerBinding;

    @Override
    protected void initViews() {
        super.initViews();
        //状态栏文字白色
        QMUIStatusBarHelper.setStatusBarDarkMode(this);

        SuperStatusBarUtil.setStatusBarColor(getWindow(), Color.BLACK);

        //隐藏视频控件标题，因为这里我们用toolbar
        binding.player.getTitleTextView().setVisibility(View.GONE);

        //隐藏频控件返回键
        binding.player.getBackButton().setVisibility(View.GONE);

        //外部辅助的旋转，帮助全屏
        orientationUtils = new OrientationUtils(this, binding.player);

        //初始化不打开外部的旋转
        orientationUtils.setEnable(false);

        binding.player.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //直接横屏
                orientationUtils.resolveByClick();

                //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
                binding.player.startWindowFullscreen(getHostActivity(), true, true);

            }
        });
    }

    @Override
    protected void initDatum() {
        super.initDatum();
        id = extraId();

        adapter = new VideoDetailAdapter(getHostActivity());

        //创建包裹适配器
        //这里实现头部是通过
        //LRecyclerView框架实现的
        //而发现界面的头部是通过BaseRecyclerViewAdapterHelper框架实现的
        //大家在学习的时候一定要搞明白
        adapterWrapper = new LRecyclerViewAdapter(adapter);

        //添加头部布局
        adapterWrapper.addHeaderView(createHeaderView());

        binding.list.setAdapter(adapterWrapper);

        //禁用下拉刷新
        binding.list.setPullRefreshEnabled(false);

        //禁用上拉加载更多
        binding.list.setLoadMoreEnabled(false);

        loadData();
    }

    /**
     * 创建头部布局
     *
     * @return
     */
    private View createHeaderView() {
        headerBinding = HeaderVideoDetailBinding.inflate(getLayoutInflater(), (ViewGroup) binding.list.getParent(), false);

        //标签流item点击
        headerBinding.tag.setOnClickListener(v -> {
            Chip chip = (Chip) v;
            Timber.d("onTagClick %s", chip.getText().toString());
        });

        //返回view
        return headerBinding.getRoot();
    }

    @Override
    protected void loadData(boolean isPlaceholder) {
        DefaultRepository.getInstance()
                .videoDetail(id)
                .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new HttpObserver<DetailResponse<Video>>() {
                    @Override
                    public void onSucceeded(DetailResponse<Video> data) {
                        preparePlay(data.getData());
                    }
                });
    }

    private void preparePlay(Video data) {
        if (SuperNetworkUtil.isMobileConnected(getApplicationContext()) && !DefaultPreferenceUtil.getInstance(getApplicationContext()).isMobileNetworkPlay()) {
            //弹出确认提示框，询问用户是否播放
            SuperDialog.newInstance(getSupportFragmentManager())
                    .setTitleRes(R.string.play_mobile_network_alert)
                    .setOnClickListener(v -> play(data), R.string.yes)
                    .setCancelButtonTextRes(R.string.no)
                    .show();
            return;
        }

        play(data);
    }

    private void play(Video data) {
        this.data = data;

        GSYVideoOptionBuilder videoOption = new GSYVideoOptionBuilder();
        videoOption
//                .setThumbImageView(imageView)
                //小屏时不触摸滑动
                .setIsTouchWiget(false)
                //音频焦点冲突时是否释放
                .setReleaseWhenLossAudio(true)
                .setRotateViewAuto(false)
                .setLockLand(false)
                .setAutoFullWithSize(true)
                .setSeekOnStart(seek)
                .setNeedLockFull(true)
                .setUrl(ResourceUtil.resourceUri(data.getUri()))
                .setCacheWithPlay(false)

                //全屏切换时不使用动画
                .setShowFullAnimation(false)
                .setVideoTitle(data.getTitle())

                //设置右下角 显示切换到全屏 的按键资源
                .setEnlargeImageRes(R.drawable.full_screen)

                //设置右下角 显示退出全屏 的按键资源
                .setShrinkImageRes(R.drawable.normal_screen)
                .setVideoAllCallBack(new GSYSampleCallBack() {
                    @Override
                    public void onPrepared(String url, Object... objects) {
                        super.onPrepared(url, objects);
                        //开始播放了才能旋转和全屏
                        orientationUtils.setEnable(true);
                        isPlay = true;
                    }

                    @Override
                    public void onQuitFullscreen(String url, Object... objects) {
                        super.onQuitFullscreen(url, objects);
                        if (orientationUtils != null) {
                            orientationUtils.backToProtVideo();
                        }
                    }
                }).setLockClickListener(new LockClickListener() {
            @Override
            public void onClick(View view, boolean lock) {
                if (orientationUtils != null) {
                    //配合下方的onConfigurationChanged
                    orientationUtils.setEnable(!lock);
                }
            }
        }).build(binding.player);

        //开始播放
        binding.player.startPlayLogic();

        //标题
        setTitle(data.getTitle());
        headerBinding.title.setText(data.getTitle());

        //发布时间
        String createdAt = SuperDateUtil.yyyyMMdd(data.getCreatedAt());
        headerBinding.createAt.setText(getResources()
                .getString(R.string.video_created_at, createdAt));

        //播放次数
        String clicksCount = getResources()
                .getString(R.string.video_clicks_count, data.getClicksCount());
        headerBinding.count.setText(clicksCount);

        //头像
        ImageUtil.showAvatar(getHostActivity(), headerBinding.icon, data.getUser().getIcon());

        //昵称
        headerBinding.nickname.setText(data.getUser().getNickname());


//        显示视频标签
//        由于服务端没有实现视频标签功能
//        所以这里就写死几个标签
//        目的是讲解如果使用流式标签布局
        ArrayList<String> tags = new ArrayList<>();
        tags.add("爱学啊");
        tags.add("测试标签1标签1");
        tags.add("测试");
        tags.add("测试标签1");
        tags.add("标签1");
        tags.add("标签1");

        headerBinding.tag.removeAllViews();

        Chip chip;
        for (String tag : tags) {
            chip = new Chip(getHostActivity());
            chip.setText(tag);

            headerBinding.tag.addView(chip);
        }

        //请求相关视频数据
        List<Object> datum = new ArrayList<>();
        datum.add("相关推荐");

        //请求相关视频数据
        //因为服务端没有实现相关视频
        //所以就请求视频列表
        DefaultRepository.getInstance()
                .videos(1)
                .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new HttpObserver<ListResponse<Video>>() {
                    @Override
                    public void onSucceeded(ListResponse<Video> data) {
                        //添加数据视频
                        datum.addAll(data.getData().getData());

                        //添加标题
                        datum.add("精彩评论");

                        //请求精彩评论
                        //由于服务端没有实现与视频相关的评论
                        //所以这里直接请求评论列表
                        DefaultRepository.getInstance()
                                .comments(new HashMap<>())
                                .to(autoDisposable(AndroidLifecycleScopeProvider.from(VideoDetailActivity.this)))
                                .subscribe(new HttpObserver<ListResponse<Comment>>() {
                                    @Override
                                    public void onSucceeded(ListResponse<Comment> data) {
                                        //添加到列表
                                        datum.addAll(data.getData().getData());

                                        //设置数据
                                        adapter.setDatum(datum);
                                    }
                                });
                    }
                });


        //清除seek
        seek = 0;
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        adapterWrapper.setOnItemClickListener((view, position) -> {
            Object data = adapter.getData(position);
            if (data instanceof Video) {
                preparePlay((Video) data);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (orientationUtils != null) {
            orientationUtils.backToProtVideo();
        }
        if (GSYVideoManager.backFromWindowFull(this)) {
            return;
        }
        super.onBackPressed();
    }


    @Override
    protected void onPause() {
        binding.player.getCurrentPlayer().onVideoPause();
        super.onPause();
        isPause = true;
    }

    @Override
    protected void onResume() {
        binding.player.getCurrentPlayer().onVideoResume(false);
        super.onResume();
        isPause = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isPlay) {
            binding.player.getCurrentPlayer().release();
        }
        if (orientationUtils != null)
            orientationUtils.releaseListener();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //如果旋转了就全屏
        if (isPlay && !isPause) {
            binding.player.onConfigurationChanged(this, newConfig, orientationUtils, true, true);
        }
    }
}