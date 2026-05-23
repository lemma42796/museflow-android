@file:Suppress("DEPRECATION")

package com.ixuea.courses.mymusic.adapter

import android.content.Context
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

/**
 * 通用FragmentPagerAdapter
 */
abstract class BaseFragmentStatePagerAdapter<T>(
    protected val context: Context,
    fm: FragmentManager,
) : FragmentStatePagerAdapter(
    fm,
    FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,
) {
    protected val datum: MutableList<T> = ArrayList()

    /**
     * 有多少个
     */
    override fun getCount(): Int {
        return datum.size
    }

    /**
     * 返回当前位置数据
     */
    fun getData(position: Int): T {
        return datum[position]
    }

    /**
     * 设置数据
     */
    fun setDatum(datum: List<T>?) {
        this.datum.clear()
        if (!datum.isNullOrEmpty()) {
            this.datum.addAll(datum)
        }
        notifyDataSetChanged()
    }
}
