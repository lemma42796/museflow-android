package com.ixuea.courses.mymusic.component.download.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.ixuea.courses.mymusic.adapter.BaseFragmentStatePagerAdapter;
import com.ixuea.courses.mymusic.component.download.fragment.DownloadedFragment;
import com.ixuea.courses.mymusic.component.download.fragment.DownloadingFragment;

/**
 * 下载界面适配器
 */
public class DownloadAdapter extends BaseFragmentStatePagerAdapter<Integer> {

    /***
     *  @param context 上下文
     * @param fm Fragment管理器
     */
    public DownloadAdapter(Context context, @NonNull FragmentManager fm) {
        super(context, fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return DownloadedFragment.newInstance();
        }

        return DownloadingFragment.newInstance();
    }
}
