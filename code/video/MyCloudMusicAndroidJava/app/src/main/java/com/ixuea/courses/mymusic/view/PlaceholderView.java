package com.ixuea.courses.mymusic.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.ixuea.courses.mymusic.databinding.ViewPlaceholderBinding;

/**
 * 界面占位控件
 * 可以实现例如：出错了等，点击重新加载
 */
public class PlaceholderView extends LinearLayout {

    private ViewPlaceholderBinding binding;

    public PlaceholderView(Context context) {
        super(context);
        init();
    }

    public PlaceholderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PlaceholderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public PlaceholderView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        binding = ViewPlaceholderBinding.inflate(LayoutInflater.from(getContext()));
        addView(binding.getRoot(), new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    /**
     * 显示提示
     */
    public void show(int titleResource, int iconResource) {
        if (titleResource != -1) {
            binding.title.setText(titleResource);
        }

        if (iconResource != -1) {
            binding.icon.setImageResource(iconResource);
        }
    }

    /**
     * 显示提示
     */
    public void show(String title, int iconResource) {
        if (title != null) {
            binding.title.setText(title);
        }

        if (iconResource != -1) {
            binding.icon.setImageResource(iconResource);
        }
    }

    /**
     * 显示提示
     */
    public void showTitle(String data) {
        binding.title.setText(data);
    }

    /**
     * 显示提示
     */
    public void showTitle(int data) {
        binding.title.setText(data);
    }

    /**
     * 显示图标
     */
    public void showIcon(int iconResource) {
        binding.icon.setImageResource(iconResource);
    }
}
