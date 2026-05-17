package com.ixuea.courses.mymusic.component.download.fragment

import android.os.Bundle
import com.ixuea.android.downloader.domain.DownloadInfo
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.component.download.adapter.DownloadingAdapter
import com.ixuea.courses.mymusic.component.download.repository.DownloadRepository
import com.ixuea.courses.mymusic.databinding.FragmentDownloadingBinding
import com.ixuea.courses.mymusic.fragment.BaseViewModelFragment
import com.ixuea.superui.toast.SuperToast
import com.ixuea.superui.util.SuperRecyclerViewUtil

/**
 * 下载中界面
 */
class DownloadingFragment : BaseViewModelFragment<FragmentDownloadingBinding>() {
    private lateinit var adapter: DownloadingAdapter
    private lateinit var repository: DownloadRepository

    override fun initViews() {
        super.initViews()
        SuperRecyclerViewUtil.initVerticalLinearRecyclerView(binding.list)
    }

    override fun initDatum() {
        super.initDatum()
        repository = DownloadRepository.getInstance()

        adapter = DownloadingAdapter(hostActivity, orm, childFragmentManager)
        binding.list.adapter = adapter

        loadData()
    }

    override fun initListeners() {
        super.initListeners()
        adapter.setOnItemClickListener { _, position ->
            val data = adapter.getData(position)

            when (data.status) {
                DownloadInfo.STATUS_NONE,
                DownloadInfo.STATUS_PAUSED,
                DownloadInfo.STATUS_ERROR -> repository.resume(data)

                else -> repository.pause(data)
            }

            showButtonStatus()
        }

        binding.download.setOnClickListener { downloadClick() }
        binding.delete.setOnClickListener { deleteClick() }
    }

    private fun deleteClick() {
        if (adapter.itemCount == 0) {
            SuperToast.show(R.string.error_not_download)
            return
        }

        adapter.datum.toList().forEach { downloadInfo ->
            repository.remove(downloadInfo)
        }

        adapter.clearData()
        showButtonStatus()
    }

    private fun downloadClick() {
        if (adapter.itemCount == 0) {
            SuperToast.show(R.string.error_not_download)
            return
        }

        if (isDownloading()) {
            pauseAll()
        } else {
            resumeAll()
        }

        showButtonStatus()
    }

    private fun showButtonStatus() {
        if (isDownloading()) {
            binding.download.setText(R.string.pause_all)
        } else {
            binding.download.setText(R.string.download_all)
        }
    }

    private fun resumeAll() {
        repository.resumeAll()
        adapter.notifyDataSetChanged()
    }

    private fun pauseAll() {
        repository.pauseAll()
        adapter.notifyDataSetChanged()
    }

    private fun isDownloading(): Boolean {
        return repository.isDownloading(adapter.datum)
    }

    override fun loadData(isPlaceholder: Boolean) {
        super.loadData(isPlaceholder)
        val downloads = repository.findDownloading()
        adapter.setDatum(downloads)

        showButtonStatus()
    }

    companion object {
        @JvmStatic
        fun newInstance(): DownloadingFragment {
            return DownloadingFragment().apply {
                arguments = Bundle()
            }
        }
    }
}
