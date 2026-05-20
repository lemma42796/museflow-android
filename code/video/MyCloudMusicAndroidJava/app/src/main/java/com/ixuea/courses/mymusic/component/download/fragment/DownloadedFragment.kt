package com.ixuea.courses.mymusic.component.download.fragment

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.component.download.ui.DownloadedUiState
import com.ixuea.courses.mymusic.component.download.ui.DownloadedViewModel
import com.ixuea.courses.mymusic.component.sheet.adapter.SongAdapter
import com.ixuea.courses.mymusic.databinding.FragmentDownloadedBinding
import com.ixuea.courses.mymusic.fragment.BaseViewModelFragment
import com.ixuea.superui.util.SuperRecyclerViewUtil
import kotlinx.coroutines.launch

/**
 * 下载完成界面
 */
class DownloadedFragment : BaseViewModelFragment<FragmentDownloadedBinding>() {
    private lateinit var adapter: SongAdapter
    private lateinit var viewModel: DownloadedViewModel
    private var handledDataVersion = 0L

    override fun initViews() {
        super.initViews()
        SuperRecyclerViewUtil.initVerticalLinearRecyclerView(binding.list)
    }

    override fun initDatum() {
        super.initDatum()
        viewModel = ViewModelProvider(this)[DownloadedViewModel::class.java]

        adapter = SongAdapter(R.layout.item_song, 1, childFragmentManager)
        binding.list.adapter = adapter
        observeDownloadedState()

        loadData()
    }

    override fun initListeners() {
        super.initListeners()
        adapter.setOnItemClickListener { _, _, position ->
            val data = adapter.getItem(position)

            musicListManager.setDatum(adapter.data)
            musicListManager.play(data)

            startMusicPlayerActivity()
        }
    }

    private fun observeDownloadedState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    render(state)
                }
            }
        }
    }

    private fun render(state: DownloadedUiState) {
        if (state.dataVersion != handledDataVersion) {
            handledDataVersion = state.dataVersion
            adapter.setNewInstance(state.songs.toMutableList())
        }
    }

    override fun loadData(isPlaceholder: Boolean) {
        super.loadData(isPlaceholder)
        viewModel.load(orm)
    }

    companion object {
        @JvmStatic
        fun newInstance(): DownloadedFragment {
            return DownloadedFragment().apply {
                arguments = Bundle()
            }
        }
    }
}
