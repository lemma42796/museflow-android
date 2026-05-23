package com.ixuea.courses.mymusic.component.download.fragment

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ixuea.android.downloader.domain.DownloadInfo
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.component.download.adapter.DownloadingAdapter
import com.ixuea.courses.mymusic.component.download.ui.DownloadingUiState
import com.ixuea.courses.mymusic.component.download.ui.DownloadingViewModel
import com.ixuea.courses.mymusic.databinding.FragmentDownloadingBinding
import com.ixuea.courses.mymusic.fragment.BaseViewModelFragment
import com.ixuea.superui.toast.SuperToast
import com.ixuea.superui.util.SuperRecyclerViewUtil
import kotlinx.coroutines.launch

/**
 * 下载中界面
 */
class DownloadingFragment : BaseViewModelFragment<FragmentDownloadingBinding>() {
    private lateinit var adapter: DownloadingAdapter
    private lateinit var viewModel: DownloadingViewModel
    private var handledDataVersion = 0L

    override fun initViews() {
        super.initViews()
        SuperRecyclerViewUtil.initVerticalLinearRecyclerView(binding.list)
    }

    override fun initDatum() {
        super.initDatum()
        viewModel = ViewModelProvider(this)[DownloadingViewModel::class.java]

        adapter = DownloadingAdapter(hostActivity, orm, childFragmentManager)
        adapter.setListener(object : DownloadingAdapter.DownloadingAdapterListener {
            override fun onDeleteClick(position: Int, data: DownloadInfo) {
                viewModel.remove(data)
            }

            override fun onDownloadTerminalState(data: DownloadInfo) {
                viewModel.onDownloadTerminalState()
            }
        })
        binding.list.adapter = adapter
        observeDownloadingState()

        loadData()
    }

    override fun initListeners() {
        super.initListeners()
        adapter.setOnItemClickListener { _, position ->
            val data = adapter.getData(position)
            viewModel.toggle(data)
        }

        binding.download.setOnClickListener { downloadClick() }
        binding.delete.setOnClickListener { deleteClick() }
    }

    private fun observeDownloadingState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    render(state)
                }
            }
        }
    }

    private fun render(state: DownloadingUiState) {
        if (state.dataVersion != handledDataVersion) {
            handledDataVersion = state.dataVersion
            adapter.setDatum(state.downloads)
            showButtonStatus(state.isDownloading)
        }
    }

    private fun deleteClick() {
        if (adapter.itemCount == 0) {
            SuperToast.show(R.string.error_not_download)
            return
        }

        viewModel.removeAll(adapter.datum.toList())
    }

    private fun downloadClick() {
        if (adapter.itemCount == 0) {
            SuperToast.show(R.string.error_not_download)
            return
        }

        if (viewModel.uiState.value.isDownloading) {
            viewModel.pauseAll()
        } else {
            viewModel.resumeAll()
        }
    }

    private fun showButtonStatus(isDownloading: Boolean) {
        if (isDownloading) {
            binding.download.setText(R.string.pause_all)
        } else {
            binding.download.setText(R.string.download_all)
        }
    }

    override fun loadData(isPlaceholder: Boolean) {
        super.loadData(isPlaceholder)
        viewModel.load()
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
