package com.ixuea.courses.mymusic.component.download.fragment

import android.os.Bundle
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.component.download.repository.DownloadRepository
import com.ixuea.courses.mymusic.component.sheet.adapter.SongAdapter
import com.ixuea.courses.mymusic.databinding.FragmentDownloadedBinding
import com.ixuea.courses.mymusic.fragment.BaseViewModelFragment
import com.ixuea.superui.util.SuperRecyclerViewUtil

/**
 * 下载完成界面
 */
class DownloadedFragment : BaseViewModelFragment<FragmentDownloadedBinding>() {
    private lateinit var adapter: SongAdapter
    private lateinit var repository: DownloadRepository

    override fun initViews() {
        super.initViews()
        SuperRecyclerViewUtil.initVerticalLinearRecyclerView(binding.list)
    }

    override fun initDatum() {
        super.initDatum()
        repository = DownloadRepository.getInstance()

        adapter = SongAdapter(R.layout.item_song, 1, childFragmentManager)
        binding.list.adapter = adapter

        loadData()
    }

    override fun initListeners() {
        super.initListeners()
        adapter.setOnItemClickListener { _, _, position ->
            val data = adapter.getItem(position) ?: return@setOnItemClickListener

            musicListManager.setDatum(adapter.data)
            musicListManager.play(data)

            startMusicPlayerActivity()
        }
    }

    override fun loadData(isPlaceholder: Boolean) {
        super.loadData(isPlaceholder)
        adapter.setNewInstance(repository.findDownloadedSongs(orm).toMutableList())
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
