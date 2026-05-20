package com.ixuea.courses.mymusic.component.player.fragment

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.component.player.adapter.MusicPlayListAdapter
import com.ixuea.courses.mymusic.databinding.FragmentDialogAudioPlayListBinding
import com.ixuea.courses.mymusic.fragment.BaseViewModelBottomSheetDialogFragment
import com.ixuea.courses.mymusic.util.PlayListUtil

/**
 * 播放列表对话框
 */
class MusicPlayListDialogFragment :
    BaseViewModelBottomSheetDialogFragment<FragmentDialogAudioPlayListBinding>() {

    private lateinit var adapter: MusicPlayListAdapter

    override fun initViews() {
        super.initViews()

        binding.list.setHasFixedSize(true)
        binding.list.addItemDecoration(
            DividerItemDecoration(activity, RecyclerView.VERTICAL)
        )
    }

    override fun initDatum() {
        super.initDatum()
        adapter = MusicPlayListAdapter(R.layout.item_play_list, musicListManager)
        binding.list.adapter = adapter

        adapter.setList(musicListManager.datum)

        showLoopModel()
        showCount()
    }

    override fun initListeners() {
        super.initListeners()
        binding.deleteAll.setOnClickListener {
            dismiss()
            musicListManager.deleteAll()
        }

        adapter.addChildClickViewIds(R.id.delete)
        adapter.setOnItemChildClickListener { _, view, position ->
            if (R.id.delete == view.id) {
                removeItem(position)
            }
        }

        binding.loopModel.setOnClickListener {
            musicListManager.changeLoopModel()
            showLoopModel()
        }

        adapter.setOnItemClickListener { _, _, position ->
            val songs = musicListManager.datum
            if (position !in songs.indices) {
                return@setOnItemClickListener
            }

            dismiss()
            musicListManager.play(songs[position])
        }

        ItemTouchHelper(object : ItemTouchHelper.Callback() {
            /**
             * 获取移动参数，主要就是告诉他是否开启滑动，什么方向可以滑动
             */
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                return makeMovementFlags(0, ItemTouchHelper.LEFT)
            }

            /**
             * 当拖拽条目时，回调
             */
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            /**
             * 当前侧滑时回调
             */
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                removeItem(viewHolder.layoutPosition)
            }

            override fun isLongPressDragEnabled(): Boolean {
                return false
            }
        }).attachToRecyclerView(binding.list)
    }

    private fun removeItem(position: Int) {
        val songs = musicListManager.datum
        if (position !in songs.indices) {
            adapter.notifyDataSetChanged()
            return
        }

        adapter.removeAt(position)
        musicListManager.delete(position)
        showCount()
    }

    /**
     * 显示循环模式
     */
    private fun showLoopModel() {
        PlayListUtil.showLoopModel(musicListManager.loopModel, binding.loopModel)
    }

    private fun showCount() {
        val count = musicListManager.datum.size
        binding.count.text = "($count)"
    }

    companion object {
        private const val TAG = "MusicPlayListDialogFragment"

        @JvmStatic
        fun newInstance(): MusicPlayListDialogFragment {
            return MusicPlayListDialogFragment().apply {
                arguments = Bundle()
            }
        }

        /**
         * 显示
         */
        @JvmStatic
        fun show(fragmentManager: FragmentManager) {
            newInstance().show(fragmentManager, TAG)
        }
    }
}
