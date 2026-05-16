package com.ixuea.courses.mymusic.component.player.activity;

import static androidx.viewpager.widget.ViewPager.SCROLL_STATE_DRAGGING;
import static androidx.viewpager.widget.ViewPager.SCROLL_STATE_IDLE;
import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ixuea.android.downloader.domain.DownloadInfo;
import com.ixuea.courses.mymusic.AppContext;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.activity.BaseTitleActivity;
import com.ixuea.courses.mymusic.component.download.listener.MyDownloadListener;
import com.ixuea.courses.mymusic.component.lyric.activity.SelectLyricActivity;
import com.ixuea.courses.mymusic.component.lyric.view.LyricListView;
import com.ixuea.courses.mymusic.component.player.fragment.MusicPlayListDialogFragment;
import com.ixuea.courses.mymusic.component.player.model.event.RecordClickEvent;
import com.ixuea.courses.mymusic.component.song.model.Song;
import com.ixuea.courses.mymusic.databinding.ActivityMusicPlayerBinding;
import com.ixuea.courses.mymusic.manager.MusicPlayerListener;
import com.ixuea.courses.mymusic.manager.MusicPlayerManager;
import com.ixuea.courses.mymusic.manager.model.event.MusicPlayListChangedEvent;
import com.ixuea.courses.mymusic.service.MusicPlayerService;
import com.ixuea.courses.mymusic.util.FileUtil;
import com.ixuea.courses.mymusic.util.PlayListUtil;
import com.ixuea.courses.mymusic.util.ResourceUtil;
import com.ixuea.courses.mymusic.util.StorageUtil;
import com.ixuea.courses.mymusic.util.SuperDateUtil;
import com.ixuea.courses.mymusic.util.SwitchDrawableUtil;
import com.ixuea.superui.toast.SuperToast;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.SoftReference;

import jp.wasabeef.glide.transformations.BlurTransformation;
import timber.log.Timber;

/**
 * 黑胶唱片界面
 */
public class MusicPlayerActivity extends BaseTitleActivity<ActivityMusicPlayerBinding> implements MusicPlayerListener, SeekBar.OnSeekBarChangeListener, LyricListView.LyricListListener {
    private MusicPlayerManager musicPlayerManager;
    private boolean isSeekTracking;

    /**
     * 下载任务
     */
    private DownloadInfo downloadInfo;

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    @Override
    protected void initViews() {
        super.initViews();
        //状态栏文字白色
        QMUIStatusBarHelper.setStatusBarDarkMode(this);

        //状态栏透明，内容显示到状态栏
        QMUIStatusBarHelper.translucent(this);
    }

    /**
     * 歌曲滚动监听器
     */
    private ViewPager2.OnPageChangeCallback pageChangeCallback = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageScrollStateChanged(int state) {
            super.onPageScrollStateChanged(state);
            if (SCROLL_STATE_DRAGGING == state) {
                //拖拽状态

                //停止当前item滚动
                Song currentSong = getMusicListManager().getDatum().get(binding.record.binding.list.getCurrentItem());
                recordRotate(currentSong, false);
            } else if (SCROLL_STATE_IDLE == state) {
                //空闲状态

                //判断黑胶唱片位置对应的音乐是否和现在播放的是一首
                Song song = getMusicListManager().getDatum().get(binding.record.binding.list.getCurrentItem());
                if (getMusicListManager().getData().getId().equals(song.getId())) {
                    //一样

                    //判断播放状态
                    if (musicPlayerManager.isPlaying()) {
                        recordRotate(song, true);
                    }
                } else {
                    //不一样

                    //播放当前位置的音乐
                    getMusicListManager().play(song);
                }
            }
        }
    };

    /**
     * 黑胶唱片点击事件
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRecordClickEvent(RecordClickEvent event) {
//        //隐藏黑胶唱片
//        binding.record.setVisibility(View.GONE);
//
//        //显示歌词
//        binding.lyricList.setVisibility(View.VISIBLE);

        binding.lyricList.setAlpha(0);
        binding.lyricList.setVisibility(View.VISIBLE);

        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            binding.lyricList.setAlpha(value);
            binding.record.setAlpha(1 - value);
        });
        animator.addListener(new AnimatorListenerAdapter() {
            /**
             * 动画结束
             * @param animation
             */
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                binding.record.setVisibility(View.GONE);
            }
        });
        animator.setDuration(300);
        animator.start();
    }

    /**
     * 音乐播放列表改变了事件
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void musicPlayListChangedEvent(MusicPlayListChangedEvent event) {
        if (event.isDeleteAll()) {
            binding.record.adapter.removeAll();
        } else {
            binding.record.adapter.remove(event.getPosition());
        }

        if (getMusicListManager().getDatum().size() == 0) {
            //没有音乐了

            //关闭播放界面
            finish();
        }
    }

    private void recordRotate(Song data, boolean isRotate) {
        data.setRotate(isRotate);
        binding.record.setPlaying(isRotate);
    }

    @Override
    protected void initDatum() {
        super.initDatum();
        musicPlayerManager = MusicPlayerService.getMusicPlayerManager(getApplicationContext());

        binding.record.initAdapter(getHostActivity());

        //设置数据
        binding.record.setData(getMusicListManager().getDatum());
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        //黑胶唱片列表滚动监听
        binding.record.binding.list.registerOnPageChangeCallback(pageChangeCallback);

        //下载按钮点击
        binding.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (downloadInfo != null) {
                    //有下载任务

                    //判断下载状态
                    if (downloadInfo.getStatus() == DownloadInfo.STATUS_COMPLETED) {
                        SuperToast.show(R.string.already_downloaded);
                    } else {
                        //其他状态
                        //可能是下载中，等待中
                        //下载失败
                        SuperToast.success(R.string.already_in_download_list);

                        if (downloadInfo.getStatus() != DownloadInfo.STATUS_DOWNLOADING || downloadInfo.getStatus() != DownloadInfo.STATUS_WAIT) {
                            AppContext.getInstance().getDownloadManager().resume(downloadInfo);
                        }
                    }
                } else {
                    //没有下载任务
                    createDownload();
                }
            }
        });

        //设置拖拽进度控件监听器
        binding.progress.setOnSeekBarChangeListener(this);

        //循环模式按钮点击
        binding.loopModel.setOnClickListener(v -> {
            //更改模式
            getMusicListManager().changeLoopModel();

            //显示循环模式
            showLoopModel();
        });

        //上一曲按钮点击
        binding.previous.setOnClickListener(v -> {
            getMusicListManager().play(getMusicListManager().previous());
        });

        //播放按钮点击
        binding.play.setOnClickListener(v -> {
            playOrPause();
        });

        //下一曲按钮点击
        binding.next.setOnClickListener(v -> {
            getMusicListManager().play(getMusicListManager().next());
        });

        //播放列表按钮点击
        binding.listButton.setOnClickListener(v -> {
            MusicPlayListDialogFragment.show(getSupportFragmentManager());
        });

        //歌词控件监听器
        binding.lyricList.setLyricListListener(this);
    }

    /**
     * 创建下载任务
     */
    private void createDownload() {
        Song data = getMusicListManager().getData();

        //转为为绝对地址
        String urlString = ResourceUtil.resourceUri(data.getUri());

        //计算保存的路径
        //路径中添加用户Id是实现多用户
        //当然这里很明显的问题是
        //如果多用户都下载一首音乐
        //会导致一首音乐会下载多次
        String path = StorageUtil.getExternalPath(getHostActivity(), sp.getUserId(), data.getTitle(), StorageUtil.MP3).getAbsolutePath();

        Timber.d("createDownload %s", path);

        //创建下载任务
        downloadInfo = new DownloadInfo.Builder()
                //设置id
                .setId(data.getId())

                //设置下载地址
                .setUrl(urlString)

                //设置保存路径
                .setPath(path)

                .build();

        //设置创建时间
        //用于显示下载中
        //下载完成列表排序
        //默认按时间
        downloadInfo.setCreateAt(System.currentTimeMillis());

        //设置下载回调
        setDownloadCallback();

        //开始下载
        AppContext.getInstance().getDownloadManager().download(downloadInfo);

        //保存业务数据
        getOrm().saveSong(data);

        SuperToast.success(R.string.add_success);
    }

    /**
     * 设置下载回调
     */
    private void setDownloadCallback() {
        //使用弱引用
        //目的是防止内存泄漏
        //因为对于监听器来说
        //界面对象销毁了
        //也没多影响
        downloadInfo.setDownloadListener(new MyDownloadListener(new SoftReference<>(this)) {
            @Override
            public void onRefresh() {
                if (getUserTag() != null && getUserTag().get() != null) {
                    MusicPlayerActivity viewHolder = (MusicPlayerActivity) getUserTag().get();
                    viewHolder.refresh();
                }
            }
        });
    }

    private void refresh() {
        if (downloadInfo != null) {
            //有下载任务
            switch (downloadInfo.getStatus()) {
                case DownloadInfo.STATUS_COMPLETED:
                    //下载完成
                    binding.download.setImageResource(R.drawable.ic_downloaded);
                    break;
                default:
                    //其他状态
                    //都显示未下载
                    //当然也可以监听下载进度
                    normalDownloadStatusUI();
                    break;
            }

            //这里不需要知道更详细的下载状态
            //所以就没有判断
            //会在下载管理里面判断
            //打印下载进度
            String start = FileUtil.formatFileSize(downloadInfo.getProgress());
            String size = FileUtil.formatFileSize(downloadInfo.getSize());
            Timber.d("download music refresh %s %s", start, size);
        } else {
            //没有下载任务
            normalDownloadStatusUI();
        }
    }

    /**
     * 未下载状态
     */
    private void normalDownloadStatusUI() {
        binding.download.setImageResource(R.drawable.ic_download);
    }

    private void showLoopModel() {
        //获取当前循环模式
        int model = getMusicListManager().getLoopModel();
        binding.loopModel.setImageResource(PlayListUtil.getLoopModelIcon(model));
    }

    /**
     * 播放或暂停
     */
    private void playOrPause() {
        if (musicPlayerManager.isPlaying()) {
            getMusicListManager().pause();
        } else {
            getMusicListManager().resume();
        }
    }

    /**
     * 选中当前音乐
     */
    private void scrollPosition() {
        int index = getMusicListManager().getDatum().indexOf(getMusicListManager().getData());
        binding.record.scrollPosition(index);
    }

    private void showLyricData() {
        binding.lyricList.setData(getMusicListManager().getData().getParsedLyric());
    }

    @Override
    protected void onResume() {
        super.onResume();
        //显示初始化数据
        showInitData();

        //显示音乐时长
        showDuration();

        //显示播放进度
        showProgress();

        //显示播放状态
        showMusicPlayStatus();

        //显示循环模式
        showLoopModel();

        //选中当前播放的音乐
        scrollPosition();

        //显示歌词数据
        showLyricData();

        //设置播放监听器
        musicPlayerManager.addMusicPlayerListener(this);
    }

    /**
     * 界面不可见了
     */
    @Override
    protected void onPause() {
        super.onPause();
        //取消播放监听器
        musicPlayerManager.removeMusicPlayerListener(this);
    }

    /**
     * 显示播放状态
     */
    private void showPlayStatus() {
        binding.play.setImageResource(R.drawable.music_play);
        binding.record.setPlaying(false);
    }

    /**
     * 显示暂停状态
     */
    private void showPauseStatus() {
        binding.play.setImageResource(R.drawable.music_pause);
        binding.record.setPlaying(true);
    }

    /**
     * 显示音乐播放状态
     */
    private void showMusicPlayStatus() {
        if (musicPlayerManager.isPlaying()) {
            showPauseStatus();
        } else {
            showPlayStatus();
        }
    }

    /**
     * 显示播放进度
     */
    private void showProgress() {
        int progress = (int) getMusicListManager().getData().getProgress();

        //格式化进度
        binding.start.setText(SuperDateUtil.ms2ms(progress));

        binding.progress.setProgress(progress);

        //显示歌词进度
        binding.lyricList.setProgress(progress);
    }

    /**
     * 显示时长
     */
    private void showDuration() {
        int end = (int) getMusicListManager().getData().getDuration();

        //格式为分钟:秒
        String endString = SuperDateUtil.ms2ms(end);
        binding.end.setText(endString);

        //设置到进度条
        binding.progress.setMax(end);
    }

    /**
     * 显示初始化数据
     */
    private void showInitData() {
        //获取当前播放的音乐
        Song data = getMusicListManager().getData();

        //显示标题
        setTitle(data.getTitle());

        //设置子标题
        toolbar.setSubtitle(data.getSinger().getNickname());

        //显示背景
//        ImageUtil.show(getHostActivity(),binding.background,data.getIcon());

        //实现背景高斯模糊效果
        RequestBuilder<Drawable> requestBuilder = Glide.with(this).asDrawable();

        if (StringUtils.isBlank(data.getIcon())) {
            //没有封面图

            //使用默认封面图
            requestBuilder.load(R.drawable.default_cover);
        } else {
            //使用真是图片
            requestBuilder.load(ResourceUtil.resourceUri(data.getIcon()));
        }

        //创建请求选项
        //传入了BlurTransformation
        //用来实现高斯模糊
        //radius:模糊半径；值越大越模糊
        //sampling:采样率；值越大越模糊
        RequestOptions options = bitmapTransform(new BlurTransformation(25, 3));

        //加载图片
        requestBuilder
                .apply(options)
                .into(new CustomTarget<Drawable>() {
                    /**
                     * 资源下载成功
                     *
                     * @param resource
                     * @param transition
                     */
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
//                        binding.background.setImageDrawable(resource);

                        //创建切换动画工具类
                        SwitchDrawableUtil switchDrawableUtil = new SwitchDrawableUtil(binding.background.getDrawable(), resource);

                        //设置drawable
                        binding.background.setImageDrawable(switchDrawableUtil.getDrawable());

                        //开始动画
                        switchDrawableUtil.start();
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });

        //下载状态

        //根据id查询是否有下载任务
        downloadInfo = AppContext.getInstance().getDownloadManager().getDownloadById(data.getId());
        if (null != downloadInfo) {
            //有下载任务

            //设置下载回调
            setDownloadCallback();
        }

        //不管有没有下载任务
        //都要刷新当前界面
        //因为要显示是否下载
        refresh();
    }

    @Override
    public void onLyricReady(Song data) {
        //显示歌词数据
        showLyricData();
    }

    @Override
    public void onPaused(Song data) {
        showPlayStatus();
    }

    @Override
    public void onPlaying(Song data) {
        showPauseStatus();
    }

    @Override
    public void onPrepared(MediaPlayer mp, Song data) {
        //显示初始化数据
        showInitData();

        //显示时长
        showDuration();

        //选中当前音乐
        scrollPosition();
    }

    @Override
    public void onProgress(Song data) {
        if (isSeekTracking) {
            return;
        }

        showProgress();
    }

    /**
     * 进度条改变了
     *
     * @param seekBar
     * @param progress 当前改变后的进度
     * @param fromUser 是否是用户触发的
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            //跳转到该位置播放
            getMusicListManager().seekTo(progress);
        }
    }

    /**
     * 开始拖拽进度条
     *
     * @param seekBar
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isSeekTracking = true;
    }

    /**
     * 停止拖拽进度条
     *
     * @param seekBar
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        isSeekTracking = false;
    }

    @Override
    public void onLyricClick() {
//        //显示黑胶唱片
//        binding.record.setVisibility(View.VISIBLE);
//
//        //隐藏歌词
//        binding.lyricList.setVisibility(View.GONE);

        //使用渐变动画实现
        binding.record.setAlpha(0);
        binding.record.setVisibility(View.VISIBLE);

        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            binding.record.setAlpha(value);
            binding.lyricList.setAlpha(1 - value);
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                binding.lyricList.setVisibility(View.GONE);
            }
        });
        animator.setDuration(300);
        animator.start();
    }

    @Override
    public boolean onLyricLongClick() {
        Song data = getMusicListManager().getData();

        startActivityExtraData(SelectLyricActivity.class, data);
        return true;
    }
}