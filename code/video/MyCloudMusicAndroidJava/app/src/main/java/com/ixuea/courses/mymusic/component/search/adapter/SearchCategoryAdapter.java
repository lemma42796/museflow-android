package com.ixuea.courses.mymusic.component.search.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.ixuea.courses.mymusic.adapter.BaseFragmentStateAdapter;
import com.ixuea.courses.mymusic.component.search.fragment.OtherFragment;
import com.ixuea.courses.mymusic.component.sheet.fragment.SheetFragment;
import com.ixuea.courses.mymusic.component.user.fragment.UserFragment;

public class SearchCategoryAdapter extends BaseFragmentStateAdapter<Integer> {

    public SearchCategoryAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            //歌单搜索结果
            return SheetFragment.newInstance(position);
        } else if (position == 1) {
            //用户搜索结果
            return UserFragment.newInstance(position);
        } else {
            //TODO 其他搜索结果
            return OtherFragment.newInstance();
        }
    }
}
