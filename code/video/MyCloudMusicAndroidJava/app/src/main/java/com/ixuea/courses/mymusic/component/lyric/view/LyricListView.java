package com.ixuea.courses.mymusic.component.lyric.view;

import static androidx.viewpager.widget.ViewPager.SCROLL_STATE_DRAGGING;
import static androidx.viewpager.widget.ViewPager.SCROLL_STATE_IDLE;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.component.lyric.adapter.LyricAdapter;
import com.ixuea.courses.mymusic.component.lyric.model.Line;
import com.ixuea.courses.mymusic.component.lyric.model.Lyric;
import com.ixuea.courses.mymusic.databinding.LyricListViewBinding;
import com.ixuea.courses.mymusic.service.MusicPlayerService;
import com.ixuea.courses.mymusic.util.Constant;
import com.ixuea.courses.mymusic.util.LyricUtil;
import com.ixuea.courses.mymusic.util.SuperDateUtil;
import com.ixuea.superui.util.DensityUtil;
import com.ixuea.superui.util.SuperViewUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import timber.log.Timber;

/**
 * 垂直显示多行歌词
 * 使用RecyclerView实现
 */
public class LyricListView extends LinearLayout {

    private LyricListViewBinding binding;
    private LinearLayoutManager layoutManager;
    private LyricAdapter lyricAdapter;
    private Lyric data;
    private int lyricLineNumber;

    /**
     * 歌词填充多个占位数据
     */
    private int lyricPlaceholderSize;

    /**
     * 歌词滚动偏移
     * 会在运行的时候动态计算
     */
    private int lyricOffsetY;

    /**
     * 是否是在拖拽中
     */
    private boolean isDrag;
    private TimerTask lyricTimerTask;
    private Timer lyricTimer;
    private Line scrollSelectedLyricLine;
    private LyricListListener lyricListListener;

    public LyricListView(Context context) {
        super(context);
        init();
    }

    public LyricListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LyricListView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public LyricListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        binding = LyricListViewBinding.inflate(LayoutInflater.from(getContext()), this, true);
        initViews();
        initDatum();
        initListeners();
    }

    private void initListeners() {
        binding.lyricPlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 取消3秒倒计时
                cancelLyricTask();

                // 隐藏拖拽UI，isDrag = false
                showScrollLyricView();
                // 关键：seekTo到拖拽选中行的起始时间
                MusicPlayerService.getListManager(getContext()).seekTo((int) scrollSelectedLyricLine.getStartTime());
            }
        });

        //添加歌词列表滚动事件
        binding.lyricList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            /**
             * 滚动状态改变了
             * 状态ViewPager一样
             *
             * @param recyclerView
             * @param newState
             */
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (SCROLL_STATE_DRAGGING == newState) {
                    //拖拽状态
                    showDragView();
                } else if (SCROLL_STATE_IDLE == newState) {
                    //空闲状态
                    prepareScrollLyricView();
                }
            }

            /**
             * 滚动了
             *
             * @param recyclerView
             * @param dx
             * @param dy
             */
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //这里的dy是当前这一次滚动的距离
                //向上滚动+
                //向下滚动-

                //当前RecyclerView可视的第一个Item位置
                //+填充占位数
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition() + lyricPlaceholderSize - 1;

                Timber.d("onPageScrolled dy:%d firstVisibleItemPosition:%d", dy, firstVisibleItemPosition);

                if (isDrag) {
                    //只有拖拽的时候才处理
                    Object data = lyricAdapter.getItem(firstVisibleItemPosition);

                    if (data instanceof String) {
                        //填充数据

                        //判断是开始还是末尾
                        if (firstVisibleItemPosition < lyricPlaceholderSize) {
                            //开始位置的填充

                            //拿到对应的歌词行数据
                            scrollSelectedLyricLine = (Line) lyricAdapter.getItem(lyricPlaceholderSize);
                        } else {
                            //末尾的填充

                            //最后一行歌词
                            int index = lyricAdapter.getItemCount() - 1 - lyricPlaceholderSize;
                            scrollSelectedLyricLine = (Line) lyricAdapter.getItem(index);
                        }
                    } else {
                        //真实数据
                        scrollSelectedLyricLine = (Line) data;
                    }

                    //显示该行歌词的起始时间（如 "01:23"）
                    binding.lyricTime.setText(SuperDateUtil.ms2ms((int) scrollSelectedLyricLine.getStartTime()));
                }
            }
        });

        binding.getRoot().setOnClickListener(v -> {
            if (lyricListListener != null) {
                lyricListListener.onLyricClick();
            }
        });

        lyricAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (lyricListListener != null) {
                lyricListListener.onLyricClick();
            }
        });

        lyricAdapter.setOnItemLongClickListener((adapter, view, position) -> lyricListListener.onLyricLongClick());
    }

    /**
     * 准备滚动歌词
     */
    private void prepareScrollLyricView() {
        //延迟几秒钟后隐藏

        //取消隐藏歌词拖拽效果原来的任务
        cancelLyricTask();

        //创建任务
        lyricTimerTask = new TimerTask() {

            @Override
            public void run() {
                //转换到主线程
                binding.lyricList.post(() -> showScrollLyricView());
            }
        };

        //创建定时器
        lyricTimer = new Timer();

        //开启一个倒计时
        lyricTimer.schedule(lyricTimerTask, Constant.LYRIC_HIDE_DRAG_TIME);
    }

    private void cancelLyricTask() {
        if (lyricTimerTask != null) {
            lyricTimerTask.cancel();
            lyricTimerTask = null;
        }

        if (lyricTimer != null) {
            lyricTimer.cancel();
            lyricTimer = null;
        }
    }

    private void showScrollLyricView() {
        isDrag = false;

        //隐藏歌词拖拽效果
        SuperViewUtil.gone(binding.lyricDragContainer);
    }

    private void showDragView() {
        if (isLyricEmpty()) {
            //没有歌词不能拖拽
            return;
        }

        //拖拽状态
        isDrag = true;

        //显示时间指示器+播放按钮
        SuperViewUtil.show(binding.lyricDragContainer);
    }

    /**
     * 是否没有歌词数据
     *
     * @return
     */
    private boolean isLyricEmpty() {
        return lyricAdapter.getItemCount() == 0;
    }

    private void initDatum() {
        lyricAdapter = new LyricAdapter(R.layout.item_lyric);
        binding.lyricList.setAdapter(lyricAdapter);
    }

    private void initViews() {
        binding.lyricList.setHasFixedSize(true);

        //设置布局管理器
        layoutManager = new LinearLayoutManager(getContext());
        binding.lyricList.setLayoutManager(layoutManager);
    }

    @Override
    protected void onDetachedFromWindow() {
        //View被销毁时取消定时任务，避免TimerTask匿名内部类持有View->Context导致泄漏
        cancelLyricTask();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (lyricOffsetY != 0) {
            return;
        }

        //获取控件高度
        lyricOffsetY = (int) (getMeasuredHeight() / 2 - (DensityUtil.dip2px(getContext(), 40) / 2));

        //计算需要多少个占位item才能填满半屏
        lyricPlaceholderSize = (int) Math.ceil(getMeasuredHeight() / 1.0 / 2 / DensityUtil.dip2px(getContext(), 40));

        next();
    }

    public void setData(Lyric data) {
        this.data = data;

        if (lyricPlaceholderSize > 0) {
            //已经计算了填充数量
            next();
        }
    }

    /**
     * 设置歌词数据
     */
    private void next() {
        if (data == null) {
            //清空原来的歌词
            lyricAdapter.setNewInstance(new ArrayList<>());
            SuperViewUtil.gone(binding.lyricList);
        } else {
            SuperViewUtil.show(binding.lyricList);

            //创建一个列表
            ArrayList<Object> datum = new ArrayList<>();

            //添加占位数据
            addLyricFillData(datum);

            datum.addAll(data.getDatum());

            //添加占位数据
            addLyricFillData(datum);

            //设置歌词是否是精确到字
            lyricAdapter.setAccurate(data.isAccurate());

            lyricAdapter.setNewInstance(datum);
        }
    }

    /**
     * 添加歌词占位数据
     */
    public void addLyricFillData(List<Object> datum) {
        for (int i = 0; i < lyricPlaceholderSize; i++) {
            datum.add("fill");
        }
    }

    /**
     * 设置播放进度
     *
     * @param progress
     */
    public void setProgress(int progress) {
        if (data == null || lyricAdapter.getData() == null || lyricAdapter.getData().size() == 0) {
            //没有歌词
            return;
        }

        if (isDrag) {
            return;
        }

        //算出当前进度对应第几行（倒序遍历，找到第一个 startTime <= progress 的行）
        int newLineNumber = LyricUtil.getLineNumber(data, progress) + lyricPlaceholderSize;
        //行号变化时才滚动（避免无意义刷新）
        if (newLineNumber != lyricLineNumber) {
            //滚动到当前行
            scrollPosition(newLineNumber);

            lyricLineNumber = newLineNumber;
        }

        //如果是精确到字歌曲
        //还需要将时间分发到item中
        //因为要持续绘制
        if (data.isAccurate()) {
            Object object = lyricAdapter.getData().get(lyricLineNumber);

            if (object instanceof Line) {
                //只有是歌词行才出来
                Line line = (Line) object;

                //获取当前时间是该行的第几个字
                int lyricCurrentWordIndex = LyricUtil.getWordIndex(line, progress);

                //获取当前时间该字
                //已经播放的时间
                float wordPlayedTime = LyricUtil.getWordPlayedTime(line, progress);

                //获取view
                View view = layoutManager.findViewByPosition(lyricLineNumber);
                if (view != null) {
                    //找到歌词控件
                    LyricLineView contentView = view.findViewById(R.id.content);

                    //将当前时间对应的字索引设置到控件
                    contentView.setLyricCurrentWordIndex(lyricCurrentWordIndex);

                    //设置当前字已经播放的时间
                    contentView.setWordPlayedTime(wordPlayedTime);

                    //刷新控件
                    contentView.onProgress();
                }
            }
        }
    }

    private void scrollPosition(int lineNumber) {
        binding.lyricList.post(new Runnable() {
            @Override
            public void run() {
                //更新选中态（只刷新旧行和新行两个item，不是全量刷新）
                lyricAdapter.setSelectedIndex(lineNumber);

//                binding.lyricList.smoothScrollToPosition(lineNumber);

                //该方法会将指定item滚动到顶部
                //offset是滚动到顶部后，在向下(+)偏移多少
                //如果我们想让一个Item在RecyclerView中间
                //那么偏移为RecyclerView.height/2

                //动态获取RecyclerView.height
                //兼容性更好
                if (lyricOffsetY > 0) {
                    layoutManager.scrollToPositionWithOffset(lineNumber, lyricOffsetY);
                }
            }
        });
    }

    public void setLyricListListener(LyricListListener lyricListListener) {
        this.lyricListListener = lyricListListener;
    }

    /**
     * 歌词列表控件监听器
     */
    public interface LyricListListener {
        /**
         * 歌词点击
         */
        default void onLyricClick() {

        }

        /**
         * 歌词长按
         */
        default boolean onLyricLongClick() {
            return false;
        }
    }
}
