package com.ixuea.courses.mymusic.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用FragmentPagerAdapter
 * 主要是创建了列表
 * 实现了通用的方法
 */
public abstract class BaseFragmentStateAdapter<T> extends FragmentStateAdapter {
    /**
     * 列表数据源
     */
    protected List<T> datum = new ArrayList<>();

    /**
     * 构造方法
     */
    public BaseFragmentStateAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public BaseFragmentStateAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public BaseFragmentStateAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    /**
     * 有多少个
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return datum.size();
    }

    /**
     * 获取当前位置的数据
     *
     * @param position
     * @return
     */
    protected T getData(int position) {
        return datum.get(position);
    }

    /**
     * 设置数据
     *
     * @param datum
     */
    public void setDatum(List<T> datum) {
        if (datum != null && datum.size() > 0) {
            this.datum.clear();
            this.datum.addAll(datum);

            //通知数据改变了
            notifyDataSetChanged();
        }
    }

    /**
     * 添加数据
     *
     * @param datum
     */
    public void addDatum(List<T> datum) {
        if (datum != null && datum.size() > 0) {
            this.datum.addAll(datum);

            //通知数据改变了
            notifyDataSetChanged();
        }
    }

    public void remove(int index) {
        this.datum.remove(index);
        notifyDataSetChanged();
    }

    public void removeAll() {
        this.datum.clear();
        notifyDataSetChanged();
    }
}
