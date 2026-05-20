package com.ixuea.courses.mymusic.component.download.activity

import com.flyco.tablayout.listener.OnTabSelectListener
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.activity.BaseTitleActivity
import com.ixuea.courses.mymusic.adapter.OnPageChangeListenerAdapter
import com.ixuea.courses.mymusic.component.download.adapter.DownloadAdapter
import com.ixuea.courses.mymusic.databinding.ActivityDownloadBinding

/**
 * 下载管理界面
 */
class DownloadActivity : BaseTitleActivity<ActivityDownloadBinding>() {
    private lateinit var adapter: DownloadAdapter

    override fun initDatum() {
        super.initDatum()
        adapter = DownloadAdapter(hostActivity, supportFragmentManager)
        binding.list.adapter = adapter
        adapter.setDatum(listOf(0, 1))

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

        binding.list.addOnPageChangeListener(object : OnPageChangeListenerAdapter() {
            override fun onPageSelected(position: Int) {
                binding.indicator.currentTab = position
            }
        })
    }
}
