package com.ixuea.courses.mymusic.component.player.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.ixuea.courses.mymusic.component.player.adapter.MusicPlayerRecordAdapter;
import com.ixuea.courses.mymusic.component.song.model.Song;
import com.ixuea.courses.mymusic.databinding.RecordPageViewBinding;
import com.ixuea.superui.util.DensityUtil;

import java.util.List;

/**
 * 黑胶唱片左右滚动页面view
 */
public class RecordPageView extends LinearLayout implements ValueAnimator.AnimatorUpdateListener {
    //黑胶唱片指针旋转
    /**
     * 黑胶唱片指针暂停的角度
     */
    private static final float THUMB_ROTATION_PAUSE = -25F;

    /**
     * 黑胶唱片指针播放的角度
     */
    private static final float THUMB_ROTATION_PLAY = 0F;

    /**
     * 黑胶唱片指针动画时间
     */
    private static final long THUMB_DURATION = 300;

    public RecordPageViewBinding binding;
    public MusicPlayerRecordAdapter adapter;
    private boolean isPlaying = true;

    /**
     * 黑胶唱片指针播放状态动画
     */
    private ObjectAnimator playThumbAnimator;

    /**
     * 黑胶唱片指针暂停状态动画
     */
    private ValueAnimator pauseThumbAnimator;

    public RecordPageView(Context context) {
        super(context);
        init();
    }

    public RecordPageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RecordPageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public RecordPageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        binding = RecordPageViewBinding.inflate(LayoutInflater.from(getContext()), this, true);
        initViews();
        initDatum();
    }

    private void initDatum() {
        setPlaying(false);
    }

    private void initViews() {
        //黑胶唱片指针旋转点
        //旋转点为15dp
        //而设置需要单位为px
        //所以要先转换
        int rotate = (int) DensityUtil.dip2px(getContext(), 15);
        binding.recordThumb.setPivotX(rotate);
        binding.recordThumb.setPivotY(rotate);

        //缓存页面数量
        binding.list.setOffscreenPageLimit(3);

        //在ViewPage2中xml中设置android:overScrollMode="never"没有用
        //要这样设置
        View child = binding.list.getChildAt(0);
        if (child instanceof RecyclerView) {
            child.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }

        //创建黑胶唱片指针的属性动画

        //从暂停到播放状态动画
        //从-25到0
        playThumbAnimator = ObjectAnimator.ofFloat(binding.recordThumb, "rotation", THUMB_ROTATION_PAUSE, THUMB_ROTATION_PLAY);

        //设置执行时间
        playThumbAnimator.setDuration(THUMB_DURATION);

        //从播放到暂停状态动画
        pauseThumbAnimator = ValueAnimator.ofFloat(THUMB_ROTATION_PLAY, THUMB_ROTATION_PAUSE);

        //设置执行时间
        pauseThumbAnimator.setDuration(THUMB_DURATION);

        //设置更新监听器
        pauseThumbAnimator.addUpdateListener(this);
    }

    public void initAdapter(FragmentActivity fragmentActivity) {
        //创建黑胶唱片列表适配器
        adapter = new MusicPlayerRecordAdapter(fragmentActivity);

        //设置到控件
        binding.list.setAdapter(adapter);
    }

    public void setData(List<Song> data) {
        //设置数据
        adapter.setDatum(data);
    }

    /**
     * 选中当前音乐
     *
     * @param index
     */
    public void scrollPosition(int index) {
        binding.list.post(new Runnable() {
            @Override
            public void run() {
                if (index != -1) {
                    //滚动到该位置
                    binding.list.setCurrentItem(index, false);
                }
            }
        });
    }

    /**
     * 设置是否在播放中状态
     *
     * @param isPlaying
     */
    public void setPlaying(boolean isPlaying) {
        if (this.isPlaying == isPlaying) {
            return;
        }

        this.isPlaying = isPlaying;
        invalidatePlayingStatus();
    }

    private void invalidatePlayingStatus() {
        if (isPlaying) {
            playThumbAnimator.start();
        } else {
            //获取黑胶唱片指针旋转的角度
            float thumbRotation = binding.recordThumb.getRotation();

            //如果不判断
            //当前已经停止了
            //还会执行动画
            //就有跳动问题
            if (THUMB_ROTATION_PAUSE == thumbRotation) {
                //已经是停止状态了

                //就返回
                return;
            }

            pauseThumbAnimator.start();
        }
    }

    /**
     * 属性动画回调
     *
     * @param animation
     */
    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        binding.recordThumb.setRotation((Float) animation.getAnimatedValue());
    }
}
