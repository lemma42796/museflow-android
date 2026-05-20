package com.ixuea.courses.mymusic.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * 通用FragmentPagerAdapter
 * 主要是创建了列表，实现了通用的方法。
 */
abstract class BaseFragmentStateAdapter<T> : FragmentStateAdapter {
    /**
     * 列表数据源
     */
    protected val datum: MutableList<T> = ArrayList()

    constructor(fragmentActivity: FragmentActivity) : super(fragmentActivity)

    constructor(fragment: Fragment) : super(fragment)

    constructor(fragmentManager: FragmentManager, lifecycle: Lifecycle) : super(
        fragmentManager,
        lifecycle
    )

    /**
     * 有多少个
     */
    override fun getItemCount(): Int {
        return datum.size
    }

    /**
     * 获取当前位置的数据
     */
    protected fun getData(position: Int): T {
        return datum[position]
    }

    /**
     * 设置数据
     */
    fun setDatum(datum: List<T>?) {
        if (!datum.isNullOrEmpty()) {
            this.datum.clear()
            this.datum.addAll(datum)
            notifyDataSetChanged()
        }
    }

    /**
     * 添加数据
     */
    fun addDatum(datum: List<T>?) {
        if (!datum.isNullOrEmpty()) {
            this.datum.addAll(datum)
            notifyDataSetChanged()
        }
    }

    fun remove(index: Int) {
        datum.removeAt(index)
        notifyDataSetChanged()
    }

    fun removeAll() {
        datum.clear()
        notifyDataSetChanged()
    }
}
