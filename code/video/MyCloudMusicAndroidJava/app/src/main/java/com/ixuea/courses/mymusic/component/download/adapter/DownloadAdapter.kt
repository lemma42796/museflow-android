package com.ixuea.courses.mymusic.component.download.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ixuea.courses.mymusic.component.download.fragment.DownloadedFragment
import com.ixuea.courses.mymusic.component.download.fragment.DownloadingFragment

/**
 * 下载界面适配器
 */
class DownloadAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = PAGE_COUNT

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) {
            DownloadedFragment.newInstance()
        } else {
            DownloadingFragment.newInstance()
        }
    }

    private companion object {
        private const val PAGE_COUNT = 2
    }
}
