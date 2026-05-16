package com.ixuea.courses.mymusic.component.user.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.adapter.BaseFragmentStatePagerAdapter;
import com.ixuea.courses.mymusic.component.feed.fragment.FeedFragment;
import com.ixuea.courses.mymusic.component.user.fragment.UserDetailAboutFragment;
import com.ixuea.courses.mymusic.component.user.fragment.UserDetailSheetFragment;

/**
 * 用户详见界面适配器
 */
public class UserDetailAdapter extends BaseFragmentStatePagerAdapter<Integer> {
    /**
     * 标题字符串id
     */
    private static final int[] titleIds = {R.string.music,
            R.string.feed,
            R.string.about_ta};

    private final String userId;

    /***
     *  @param context 上下文
     * @param fm Fragment管理器
     */
    public UserDetailAdapter(Context context, @NonNull FragmentManager fm, String userId) {
        super(context, fm);
        this.userId = userId;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return UserDetailSheetFragment.newInstance(userId);
            case 1:
                return FeedFragment.newInstance(userId);
            default:
                return UserDetailAboutFragment.newInstance(userId);
        }
    }

    /**
     * 返回标题
     *
     * @param position
     * @return
     */
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        //获取字符串id
        int resourceId = titleIds[position];

        //获取字符串
        return context.getResources().getString(resourceId);
    }
}
