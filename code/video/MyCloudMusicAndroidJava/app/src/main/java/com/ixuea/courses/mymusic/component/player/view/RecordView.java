package com.ixuea.courses.mymusic.component.player.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.ixuea.courses.mymusic.databinding.RecordViewBinding;

/**
 * 黑胶唱片view
 */
public class RecordView extends LinearLayout {
    /**
     * 每16毫秒旋转的角度
     * 16毫秒是通过
     * 每秒60帧计算出来的
     * 也就是1000/60=16
     * 也就是说绘制一帧要在16毫秒中完成
     * 不然就能感觉卡顿
     * 用秒表测转一圈的时间
     */
    private static final float ROTATION_PER = 0.2304F;

    public RecordViewBinding binding;

    /**
     * 旋转的角度
     */
    private float recordRotation;

    public RecordView(Context context) {
        super(context);
        init();
    }

    public RecordView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RecordView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public RecordView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        binding = RecordViewBinding.inflate(LayoutInflater.from(getContext()), this, true);
    }

    /**
     * 增量旋转
     */
    public void incrementRotate() {
        //判断旋转角度边界
        if (recordRotation >= 360) {
            //就设置为0
            recordRotation = 0;
        }

        //加上每次旋转的角度
        recordRotation += ROTATION_PER;

        //旋转
        binding.content.setRotation(recordRotation);
    }
}
