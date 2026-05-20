package com.ixuea.courses.mymusic.component.download.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.ixuea.courses.mymusic.adapter.BaseFragmentStatePagerAdapter
import com.ixuea.courses.mymusic.component.download.fragment.DownloadedFragment
import com.ixuea.courses.mymusic.component.download.fragment.DownloadingFragment

/**
 * 下载界面适配器
 */
class DownloadAdapter(
    context: Context,
    fm: FragmentManager,
) : BaseFragmentStatePagerAdapter<Int>(context, fm) {
    override fun getItem(position: Int): Fragment {
        return if (position == 0) {
            DownloadedFragment.newInstance()
        } else {
            DownloadingFragment.newInstance()
        }
    }
}
