package com.ixuea.courses.mymusic.component.guide.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.ixuea.courses.mymusic.adapter.BaseFragmentStatePagerAdapter;
import com.ixuea.courses.mymusic.component.guide.fragment.GuideFragment;

/**
 * 引导界面适配器
 */
public class GuideAdapter extends BaseFragmentStatePagerAdapter<Integer> {

    /***
     *  @param context 上下文
     * @param fm Fragment管理器
     */
    public GuideAdapter(Context context, @NonNull FragmentManager fm) {
        super(context, fm);
    }

    /**
     * 返回当前位置Fragment
     *
     * @param position
     * @return
     */
    @NonNull
    @Override
    public Fragment getItem(int position) {
        return GuideFragment.newInstance(getData(position));
    }
}
