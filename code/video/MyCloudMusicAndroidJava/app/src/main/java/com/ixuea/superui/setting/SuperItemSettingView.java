package com.ixuea.superui.setting;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.databinding.SuperItemSettingViewBinding;
import com.ixuea.superui.util.SuperViewUtil;

/**
 * 设置Item view
 */
public class SuperItemSettingView extends LinearLayout {

    private SuperItemSettingViewBinding binding;

    public SuperItemSettingView(Context context) {
        this(context, null);
    }

    public SuperItemSettingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SuperItemSettingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SuperItemSettingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr, defStyleRes);
    }

    private void init(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        binding = SuperItemSettingViewBinding.inflate(LayoutInflater.from(getContext()), this, true);
        initViews();

        final Resources.Theme theme = getContext().getTheme();

        if (attrs != null) {
            TypedArray typedArray = theme.obtainStyledAttributes(attrs, R.styleable.SuperItemSettingView, defStyleAttr, defStyleRes);

            //图标
            Drawable d = typedArray.getDrawable(R.styleable.SuperItemSettingView_src);
            if (d != null) {
                binding.icon.setImageDrawable(d);
                SuperViewUtil.show(binding.icon);
            }

            //标题
            String text = typedArray.getString(R.styleable.SuperItemSettingView_title_text);
            setTitle(text);

            //更多文本
            text = typedArray.getString(R.styleable.SuperItemSettingView_more_text);
            setMoreText(text);

            //更多图标
            d = typedArray.getDrawable(R.styleable.SuperItemSettingView_more_src);
            if (d != null) {
                binding.moreIcon.setImageDrawable(d);
            }

        }
    }

    /**
     * 设置更多文本
     *
     * @param data
     */
    public void setMoreText(String data) {
        binding.more.setText(data);
    }

    /**
     * 设置标题
     *
     * @param data
     */
    public void setTitle(String data) {
        binding.title.setText(data);
    }

    /**
     * 设置标题
     *
     * @param data
     */
    public void setTitle(@StringRes int data) {
        binding.title.setText(data);
    }

    /**
     * 设置图标
     *
     * @param data
     */
    public void setIcon(@DrawableRes int data) {
        binding.icon.setImageResource(data);
    }

    private void initViews() {

    }
}
