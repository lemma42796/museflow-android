package com.ixuea.courses.mymusic.component.lyric.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.component.lyric.model.Line;
import com.ixuea.courses.mymusic.util.TextUtil;
import com.ixuea.superui.util.DensityUtil;

/**
 * 一行歌词控件
 * <p>
 * TODO: 歌词超出一行，滚动显示
 */
public class LyricLineView extends View {
    /**
     * 默认歌词字大小
     * 单位：dp
     */
    private static final float DEFAULT_LYRIC_TEXT_SIZE = 16;

    /**
     * 默认歌词颜色
     */
    private static final int DEFAULT_LYRIC_TEXT_COLOR = Color.WHITE;

    /**
     * 默认歌词高亮颜色
     */
    private static final int DEFAULT_LYRIC_SELECTED_TEXT_COLOR = Color.parseColor("#d6271c");

    /**
     * 歌词位置 水平居左，垂直居中
     */
    private static final int GRAVITY_LEFT = 0x01;

    /**
     * 歌词位置 垂直水平居中
     */
    private static final int GRAVITY_CENTER = 0x10;

    /**
     * 当前歌词行
     */
    private Line data;

    /**
     * 默认歌词画笔
     */
    private Paint backgroundTextPaint;

    /**
     * 复用的FontMetrics，避免onDraw中频繁分配
     */
    private final Paint.FontMetrics fontMetrics = new Paint.FontMetrics();

    /**
     * 歌词颜色
     */
    private int lyricTextColor;

    /**
     * 文字大小
     */
    private int lyricTextSize;

    /**
     * 是否选中
     */
    private boolean lineSelected;

    /**
     * 高亮画笔
     */
    private Paint foregroundTextPaint;

    /**
     * 高亮歌词颜色
     */
    private int lyricSelectedTextColor;

    /**
     * 是否是精确到字歌词
     */
    private boolean accurate;

    /**
     * 当前播放时间点，在该行的第几个字
     */
    private int lyricCurrentWordIndex;

    /**
     * 当前行歌词已经唱过的宽度，也就是歌词高亮的宽度
     */
    private float lineLyricPlayedWidth;

    /**
     * 当前字，已经播放的时间
     */
    private float wordPlayedTime;

    private StringBuilder stringBuilder = new StringBuilder();

    /**
     * 歌词位置
     */
    private int lyricGravity;

    public LyricLineView(Context context) {
        super(context);
        init(null);
    }

    public LyricLineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public LyricLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public LyricLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        lyricTextColor = DEFAULT_LYRIC_TEXT_COLOR;
        lyricTextSize = (int) DensityUtil.dip2px(getContext(), DEFAULT_LYRIC_TEXT_SIZE);
        lyricSelectedTextColor = DEFAULT_LYRIC_SELECTED_TEXT_COLOR;

        //解析自定义属性
        if (attrs != null) {
            //获取属性值
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.LyricLineView);

            //这个属性名为
            //declare-styleable name名称+属性名
            //获取歌词大小
            lyricTextSize = (int) typedArray.getDimension(R.styleable.LyricLineView_text_size, lyricTextSize);

            //歌词默认颜色
            lyricTextColor = typedArray.getColor(R.styleable.LyricLineView_text_color, lyricTextColor);

            //歌词高亮颜色
            lyricSelectedTextColor = typedArray.getColor(R.styleable.LyricLineView_selected_text_color, lyricSelectedTextColor);

            //歌词位置
            lyricGravity = typedArray.getInt(R.styleable.LyricLineView_gravity, GRAVITY_CENTER);

            //回收TypedArray，避免缓存条目无法复用
            typedArray.recycle();
        }

        //初始化画笔

        //默认歌词画笔
        //以下内容都是Java/Android绘图API知识
        backgroundTextPaint = new Paint();

        //设置图像防抖动
        backgroundTextPaint.setDither(true);

        //设置抗锯齿
        backgroundTextPaint.setAntiAlias(true);

        //设置文本颜色
        backgroundTextPaint.setColor(lyricTextColor);

        //高亮画笔
        foregroundTextPaint = new Paint();
        foregroundTextPaint.setDither(true);
        foregroundTextPaint.setAntiAlias(true);

        updateTextColor();

        updateTextSize();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //保存状态
        canvas.save();

        if (isEmptyLyric()) {
            //如果没有歌词

            //就绘制默认提示文本
            //drawDefaultText(canvas);

        } else {
            //绘制真实歌词
            drawLyricText(canvas);
        }

        //恢复状态
        canvas.restore();
    }

    /**
     * 绘制真实歌词
     *
     * @param canvas
     */
    private void drawLyricText(Canvas canvas) {
        //获取歌词的宽高
        float textWidth = TextUtil.getTextWidth(backgroundTextPaint, data.getData());
        float textHeight = TextUtil.getTextHeight(backgroundTextPaint);

        //水平居中坐标
        float centerX = getCenterX(textWidth);

        //垂直居中坐标（复用FontMetrics实例，避免每帧分配）
        backgroundTextPaint.getFontMetrics(fontMetrics);

        float centerY = (getMeasuredHeight() - textHeight) / 2 + Math.abs(fontMetrics.top);

        //1、先画完整的白色底层文字（永远完整显示）
        canvas.drawText(data.getData(), centerX, centerY, backgroundTextPaint);

        if (lineSelected) {
            //选中

            if (accurate) {
                //精确到字
                if (lyricCurrentWordIndex == -1) {
                    //该行已经播放完了
                    lineLyricPlayedWidth = textWidth;
                } else {
                    //歌词所有字
                    String[] lyricWords = data.getWords();

                    //歌词所有字对应时间
                    int[] wordDurations = data.getWordDurations();

                    //2、获取当前时间前面的文字，算出当前字之前所有字的像素宽度
                    String beforeText = getBeforeText(data, lyricCurrentWordIndex);
                    float beforeTextWidth = TextUtil.getTextWidth(foregroundTextPaint, beforeText);

                    //当前字
                    String currentWord = lyricWords[lyricCurrentWordIndex];

                    //当前字的完整像素宽度
                    float currentWordTextWidth = TextUtil.getTextWidth(foregroundTextPaint, currentWord);

                    //3、计算"当前字内部"已唱部分的像素宽度
                    //关键公式：字的像素宽度 * （已播放时长 / 字的总时长）
                    float currentWordPlayedWidth = currentWordTextWidth / wordDurations[lyricCurrentWordIndex] * wordPlayedTime;

                    //4、整行已唱宽度 = 前面字的宽度 + 当前字内部已唱宽度
                    lineLyricPlayedWidth = beforeTextWidth + currentWordPlayedWidth;
                }

                //绘制矩形宽高
//                canvas.drawRect(centerX,0,centerX+lineLyricPlayedWidth,getMeasuredHeight(),testPaint);

                //用 clipRect 裁剪画布，只保留左边已唱区域
                canvas.clipRect(centerX, 0, centerX + lineLyricPlayedWidth, getMeasuredHeight());
            }

            //在裁剪后的画布上画红色高亮文字，虽然drawText画的是完整文字，但clipRect之外的部分不会显示
            canvas.drawText(data.getData(), centerX, centerY, foregroundTextPaint);
        }
    }

    /**
     * 获取该索引前面字符串
     *
     * @param data
     * @param index
     * @return
     */
    private String getBeforeText(Line data, int index) {
        stringBuilder.setLength(0);

        for (int i = 0; i < index; i++) {
            stringBuilder.append(data.getWords()[i]);
        }

        return stringBuilder.toString();
    }

    /**
     * 获取歌词在水平方向上的中心点
     *
     * @param textWidth
     * @return
     */
    private float getCenterX(float textWidth) {
        switch (lyricGravity) {
            case GRAVITY_LEFT:
                return 0;
            default:
                return (getMeasuredWidth() - textWidth) / 2;
        }
    }

    private boolean isEmptyLyric() {
        return data == null;
    }

    public void setData(Line data) {
        this.data = data;

        invalidate();
    }

    public void setLyricTextColor(int lyricTextColor) {
        this.lyricTextColor = lyricTextColor;

        backgroundTextPaint.setColor(lyricTextColor);

        invalidate();
    }

    public void setLineSelected(boolean lineSelected) {
        this.lineSelected = lineSelected;
    }

    public void setAccurate(boolean accurate) {
        this.accurate = accurate;
    }

    /**
     * 歌词进度
     */
    public void onProgress() {
        if (!isEmptyLyric()) {
            //有歌词就刷新控件
            invalidate();
        }
    }

    /**
     * 设置当前字索引
     *
     * @param lyricCurrentWordIndex
     */
    public void setLyricCurrentWordIndex(int lyricCurrentWordIndex) {
        this.lyricCurrentWordIndex = lyricCurrentWordIndex;
    }

    /**
     * 设置当前字已经播放的时间
     *
     * @param wordPlayedTime
     */
    public void setWordPlayedTime(float wordPlayedTime) {
        this.wordPlayedTime = wordPlayedTime;
    }

    /**
     * 设置歌词高亮颜色
     *
     * @param lyricSelectedTextColor
     */
    public void setLyricSelectedTextColor(int lyricSelectedTextColor) {
        this.lyricSelectedTextColor = lyricSelectedTextColor;

        //更新文本颜色
        updateTextColor();
    }

    /**
     * 重新信息歌词高亮颜色
     */
    private void updateTextColor() {
        foregroundTextPaint.setColor(lyricSelectedTextColor);

        //刷新一次
        invalidate();
    }

    /**
     * 减小字体大小
     *
     * @return
     */
    public int decrementTextSize() {
        lyricTextSize--;
        updateTextSize();
        return lyricTextSize;
    }

    /**
     * 增大字体大小
     *
     * @return
     */
    public int incrementTextSize() {
        lyricTextSize++;
        updateTextSize();
        return lyricTextSize;
    }

    /**
     * 重新设置字体大小
     */
    private void updateTextSize() {
        backgroundTextPaint.setTextSize(lyricTextSize);
        foregroundTextPaint.setTextSize(lyricTextSize);

        //刷新一次
        invalidate();
    }

    /**
     * 设置歌词字体大小
     *
     * @param lyricTextSize
     */
    public void setLyricTextSize(int lyricTextSize) {
        this.lyricTextSize = lyricTextSize;

        //更新文本大小
        updateTextSize();
    }
}
