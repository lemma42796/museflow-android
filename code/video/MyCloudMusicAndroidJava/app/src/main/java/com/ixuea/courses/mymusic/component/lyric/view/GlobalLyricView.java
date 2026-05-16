package com.ixuea.courses.mymusic.component.lyric.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.component.lyric.model.Line;
import com.ixuea.courses.mymusic.component.lyric.model.Lyric;
import com.ixuea.courses.mymusic.component.song.model.Song;
import com.ixuea.courses.mymusic.databinding.ViewGlobalLyricBinding;
import com.ixuea.courses.mymusic.util.LyricUtil;
import com.ixuea.courses.mymusic.util.PreferenceUtil;

import me.shihao.library.XRadioGroup;

/**
 * 全局（桌面）歌词
 */
public class GlobalLyricView extends LinearLayout implements XRadioGroup.OnCheckedChangeListener {
    /**
     * 歌词颜色列表
     */
    private static final int[] LYRIC_COLORS = new int[]{
            R.color.lyric_color0,
            R.color.lyric_color1,
            R.color.lyric_color2,
            R.color.lyric_color3,
            R.color.lyric_color4,
    };

    /**
     * 歌词颜色单选按钮Id
     */
    private static final int[] RADIO_BUTTONS = new int[]{
            R.id.radio_button0,
            R.id.radio_button1,
            R.id.radio_button2,
            R.id.radio_button3,
            R.id.radio_button4,
    };

    /**
     * 偏好设置工具栏
     */
    private PreferenceUtil sp;

    private ViewGlobalLyricBinding binding;

    /**
     * 歌词控件拖拽监听器
     */
    private OnGlobalLyricDragListener lyricDragListener;

    /**
     * 全局歌词view监听器
     */
    private GlobalLyricListener globalLyricListener;

    private GlobalLyricOtherListener globalLyricOtherListener;

    /**
     * 是否拦截事件
     */
    private boolean isIntercept;

    /**
     * 按下x坐标
     */
    private float lastX;

    /**
     * 按下y坐标
     */
    private float lastY;

    /**
     * 最小滑动距离
     * 目的是过滤不必要的滑动
     */
    private float touchSlop;

    public GlobalLyricView(Context context) {
        super(context);
        init();
    }

    public GlobalLyricView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GlobalLyricView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public GlobalLyricView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        binding = ViewGlobalLyricBinding.inflate(LayoutInflater.from(getContext()), this, true);
        initViews();

        initDatum();
        initListeners();
    }

    private void initViews() {
        //设置背景
        setBackground();

        //设置第一行歌词始终是选中状态
        binding.lyricLine1.setLineSelected(true);
    }

    private void initDatum() {
        //初始化偏好设置工具栏
        sp = PreferenceUtil.getInstance(getContext());

        //获取歌词颜色索引
        int lyricTextColorIndex = sp.getGlobalLyricTextColorIndex();

        updateLyricTextColor(lyricTextColorIndex);

        //获取对应的单选按钮id
        int radioButtonId = RADIO_BUTTONS[lyricTextColorIndex];
        binding.radioGroup.check(radioButtonId);

        //获取偏好设置中的字体大小
        int lyricTextSize = sp.getGlobalLyricTextSize();

        //设置到控件
        binding.lyricLine1.setLyricTextSize(lyricTextSize);
        binding.lyricLine2.setLyricTextSize(lyricTextSize);

        binding.lyricLine1.setLyricTextColor(getContext().getColor(R.color.black165));
        binding.lyricLine2.setLyricTextColor(getContext().getColor(R.color.black165));

        //获取view的配置
        ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());

        //获取最小滑动距离
        touchSlop = viewConfiguration.getScaledTouchSlop();
    }

    private void initListeners() {
        //歌词view点击
        OnClickListener thisClickListener = v -> {
            if (binding.logo.getVisibility() == View.VISIBLE) {
                //标准模式

                //显示简单模式
                simpleStyle();
            } else {
                //简单模式

                //标准模式
                normalStyle();
            }
        };

        this.setOnClickListener(thisClickListener);

        //logo点击
        binding.logo.setOnClickListener(v -> {
            globalLyricListener.onLogoClick();
        });

        //关闭点击
        binding.close.setOnClickListener(v -> {
            globalLyricOtherListener.closeLyric();
        });

        //锁定点击
        binding.lock.setOnClickListener(v -> {
            globalLyricListener.onLockClick();
        });

        //上一首点击
        binding.previous.setOnClickListener(v -> {
            globalLyricListener.onPreviousClick();
        });

        //播放点击
        binding.play.setOnClickListener(v -> {
            globalLyricListener.onPlayClick();
        });

        //下一首点击
        binding.next.setOnClickListener(v -> {
            globalLyricListener.onNextClick();
        });

        //设置点击
        binding.settings.setOnClickListener(v -> {
            if (binding.lyricEditContainer.getVisibility() == View.VISIBLE) {
                binding.lyricEditContainer.setVisibility(GONE);
            } else {
                binding.lyricEditContainer.setVisibility(VISIBLE);
            }
        });

        //歌词颜色单选按钮组
        binding.radioGroup.setOnCheckedChangeListener(this);

        //减小歌词字体大小按钮点击
        binding.fontSizeSmall.setOnClickListener(v -> {
            //减小歌词字体大小
            int currentSize = binding.lyricLine1.decrementTextSize();

            //设置第二个歌词控件字体大小
            setLyricTextSize(currentSize);

            //保存到配置文件
            sp.setGlobalLyricTextSize(currentSize);
        });

        //增大歌词字体大小按钮点击
        binding.fontSizeLarge.setOnClickListener(v -> {
            //增大歌词字体大小
            int currentSize = binding.lyricLine1.incrementTextSize();

            //设置第二个歌词控件字体大小
            setLyricTextSize(currentSize);

            //保存到配置文件
            sp.setGlobalLyricTextSize(currentSize);
        });
    }

    /**
     * 设置第二个歌词控件字体大小
     *
     * @param currentSize
     */
    private void setLyricTextSize(int currentSize) {
        //设置到第二个歌词控件
        binding.lyricLine2.setLyricTextSize(currentSize);
    }

    /**
     * 设置背景
     */
    private void setBackground() {
        setBackgroundColor(getContext().getColor(R.color.global_lyric_background));
    }

    /**
     * 标准样式
     * 都显示
     */
    public void normalStyle() {
        //设置背景为半透明
        setBackground();

        //显示logo
        binding.logo.setVisibility(VISIBLE);

        //显示关闭按钮
        binding.close.setVisibility(View.VISIBLE);

        //显示播放控制容器
        binding.playContainer.setVisibility(View.VISIBLE);
    }

    /**
     * 简单样式
     * 只有歌词
     */
    public void simpleStyle() {
        //背景设置为透明
        setBackgroundColor(getContext().getColor(R.color.transparent));

        //隐藏logo
        binding.logo.setVisibility(View.GONE);

        //隐藏关闭按钮
        binding.close.setVisibility(GONE);

        //隐藏播放控制容器
        binding.playContainer.setVisibility(GONE);

        //隐藏编辑容器
        binding.lyricEditContainer.setVisibility(GONE);
    }

    public void clearLyric() {
        binding.lyricLine1.setData(null);
        binding.lyricLine2.setData(null);
    }

    /**
     * 设置播放状态
     *
     * @param playing
     */
    public void setPlay(boolean playing) {
        binding.play.setImageResource(playing ? R.drawable.music_pause : R.drawable.music_play);
    }

    /**
     * 设置歌词是否是精确模式
     *
     * @param accurate
     */
    public void setAccurate(boolean accurate) {
        binding.lyricLine1.setAccurate(accurate);
    }

    /**
     * 音乐进度回调
     *
     * @param data
     */
    public void onProgress(Song data) {
        Lyric lyric = data.getParsedLyric();

        long progress = data.getProgress();

        //获取当前进度对应的歌词行
        Line line = LyricUtil.getLyricLine(lyric, progress);

        //设置数据
        binding.lyricLine1.setData(line);

        //如果是精确到字歌词
        //就需要计算已经播放到那个字相关信息
        if (lyric.isAccurate()) {
            //获取当前时间是该行的第几个字
            int lyricCurrentWordIndex = LyricUtil.getWordIndex(line, progress);

            //获取当前时间该字
            //已经播放的时间
            float wordPlayedTime = LyricUtil.getWordPlayedTime(line, progress);

            //将当前时间对应的字索引设置到控件
            binding.lyricLine1.setLyricCurrentWordIndex(lyricCurrentWordIndex);

            //设置当前字已经播放的时间
            binding.lyricLine1.setWordPlayedTime(wordPlayedTime);

            //刷新控件
            binding.lyricLine1.onProgress();
        }

        //第二行歌词控件

        //获取当前时间对应的行数
        int lineNumber = LyricUtil.getLineNumber(lyric, (int) progress);

        if (lineNumber < lyric.getDatum().size() - 1) {
            //还有下一行歌词

            //获取下一行歌词
            Line nextLine = lyric.getDatum().get(lineNumber + 1);

            //设置到第二个歌词控件
            binding.lyricLine2.setData(nextLine);
        }
    }

    /**
     * 用来判断是否拦截该事件
     *
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        isIntercept = false;
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                isIntercept = false;

                //获取第一次按下的坐标
                lastX = ev.getX();
                lastY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(ev.getY() - lastY) > touchSlop) {
                    //如果在y轴方向滑动的距离大于最小滑动距离

                    //拦截
                    isIntercept = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                isIntercept = false;
                break;
        }
        return isIntercept;
    }

    /**
     * 如果当前控件拦截了事件
     * 就会执行现在这个方法
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            //这里监听不到ACTION_DOWN
            //因为onInterceptTouchEvent方法没有拦截
            case MotionEvent.ACTION_MOVE:
                //滑动的距离
                float distanceX = event.getX() - lastX;
                float distanceY = event.getY() - lastY;

                if (Math.abs(distanceY) > touchSlop) {
                    //要处理事件

                    //获取绝对坐标（包含状态栏）
                    float rawY = event.getRawY();

                    float moveY = rawY - lastY;

                    //将拖拽的位置回调到外部
                    lyricDragListener.onGlobalLyricDrag((int) moveY);
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    public void setLyricDragListener(OnGlobalLyricDragListener lyricDragListener) {
        this.lyricDragListener = lyricDragListener;
    }

    public void setGlobalLyricListener(GlobalLyricListener globalLyricListener) {
        this.globalLyricListener = globalLyricListener;
    }

    /**
     * 颜色单选按钮更改了
     *
     * @param group
     * @param checkedId
     */
    @Override
    public void onCheckedChanged(XRadioGroup group, int checkedId) {
        //通过tag解析出索引
        String tag = (String) group.findViewById(checkedId).getTag();

        //将tag解析为int
        int index = Integer.parseInt(tag);

        //更改歌词颜色
        updateLyricTextColor(index);

        //保存到偏好设置
        sp.setGlobalLyricTextColorIndex(index);
    }

    /**
     * 更新歌词颜色
     *
     * @param index
     */
    private void updateLyricTextColor(int index) {
        //获取颜色
        int color = getContext().getColor(LYRIC_COLORS[index]);

        //设置颜色到歌词控件
        binding.lyricLine1.setLyricSelectedTextColor(color);
    }

    public void setGlobalLyricOtherListener(GlobalLyricOtherListener globalLyricOtherListener) {
        this.globalLyricOtherListener = globalLyricOtherListener;
    }

    /**
     * 全局歌词拖拽接口
     */
    public interface OnGlobalLyricDragListener {
        /**
         * 拖拽的方法
         *
         * @param y y轴方向上移动的距离
         */
        void onGlobalLyricDrag(int y);
    }

    /**
     * 全局歌词View监听器
     */
    public interface GlobalLyricListener {
        /**
         * logo点击
         */
        void onLogoClick();

        /**
         * 锁定点击
         */
        void onLockClick();

        /**
         * 上一首点击
         */
        void onPreviousClick();

        /**
         * 播放点击
         */
        void onPlayClick();

        /**
         * 下一首点击
         */
        void onNextClick();
    }

    public interface GlobalLyricOtherListener {
        /**
         * 关闭歌词
         */
        void closeLyric();
    }
}
