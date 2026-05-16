package com.ixuea.courses.mymusic.component.main.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.ixuea.courses.mymusic.adapter.BaseFragmentStatePagerAdapter;
import com.ixuea.courses.mymusic.component.discovery.fragment.DiscoveryFragment;
import com.ixuea.courses.mymusic.component.feed.fragment.FeedFragment;
import com.ixuea.courses.mymusic.component.me.fragment.MeFragment;
import com.ixuea.courses.mymusic.component.room.fragment.RoomFragment;
import com.ixuea.courses.mymusic.component.video.fragment.VideoFragment;

/**
 * 主界面ViewPager的Adapter
 */
public class MainAdapter extends BaseFragmentStatePagerAdapter<Integer> {
    /***
     *  @param context 上下文
     * @param fm Fragment管理器
     */
    public MainAdapter(Context context, @NonNull FragmentManager fm) {
        super(context, fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 1:
                return VideoFragment.newInstance();
            case 2:
                return MeFragment.newInstance();
            case 3:
                return FeedFragment.newInstance();
            case 4:
                return RoomFragment.newInstance();
            default:
                return DiscoveryFragment.newInstance();
        }
    }
}
