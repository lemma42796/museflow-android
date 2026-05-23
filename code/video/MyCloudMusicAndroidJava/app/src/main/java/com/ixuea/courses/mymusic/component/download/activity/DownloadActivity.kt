package com.ixuea.courses.mymusic.component.download.activity

import androidx.viewpager2.widget.ViewPager2
import com.flyco.tablayout.listener.OnTabSelectListener
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.activity.BaseTitleActivity
import com.ixuea.courses.mymusic.component.download.adapter.DownloadAdapter
import com.ixuea.courses.mymusic.databinding.ActivityDownloadBinding

/**
 * 下载管理界面
 */
class DownloadActivity : BaseTitleActivity<ActivityDownloadBinding>() {
    private lateinit var adapter: DownloadAdapter
    private val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            binding.indicator.currentTab = position
        }
    }

    override fun initDatum() {
        super.initDatum()
        adapter = DownloadAdapter(hostActivity)
        binding.list.adapter = adapter

        val indicatorTitles = arrayOf(
            getString(R.string.download_complete),
            getString(R.string.downloading),
        )
        binding.indicator.setTabData(indicatorTitles)
    }

    override fun initListeners() {
        super.initListeners()
        binding.indicator.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                binding.list.currentItem = position
            }

            override fun onTabReselect(position: Int) {
            }
        })

        binding.list.registerOnPageChangeCallback(pageChangeCallback)
    }

    override fun onDestroy() {
        binding.list.unregisterOnPageChangeCallback(pageChangeCallback)
        super.onDestroy()
    }
}
