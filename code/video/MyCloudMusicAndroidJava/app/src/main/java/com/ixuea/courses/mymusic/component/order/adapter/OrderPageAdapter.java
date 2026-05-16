package com.ixuea.courses.mymusic.component.order.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.ixuea.courses.mymusic.adapter.BaseFragmentStateAdapter;
import com.ixuea.courses.mymusic.component.order.fragment.OrderFragment;

/**
 * 订单主界面ViewPager的Adapter
 */
public class OrderPageAdapter extends BaseFragmentStateAdapter<Integer> {
    public OrderPageAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return OrderFragment.newInstance(getData(position));
    }
}
