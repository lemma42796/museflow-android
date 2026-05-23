package com.ixuea.courses.mymusic.component.music.activity

import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.chad.library.adapter.base.BaseQuickAdapter
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.activity.BaseTitleActivity
import com.ixuea.courses.mymusic.component.music.domain.ObserveLocalMusicScanCompleteUseCase
import com.ixuea.courses.mymusic.component.music.fragment.MusicSortDialogFragment
import com.ixuea.courses.mymusic.component.sheet.adapter.SongAdapter
import com.ixuea.courses.mymusic.databinding.ActivityLocalMusicBinding
import com.ixuea.superui.toast.SuperToast
import com.ixuea.superui.util.SuperRecyclerViewUtil
import kotlinx.coroutines.launch

/**
 * 本地音乐界面
 */
class LocalMusicActivity : BaseTitleActivity<ActivityLocalMusicBinding>() {
    private lateinit var adapter: SongAdapter
    private var editMenuItem: MenuItem? = null
    private val observeScanComplete = ObserveLocalMusicScanCompleteUseCase()

    override fun initViews() {
        super.initViews()
        SuperRecyclerViewUtil.initVerticalLinearRecyclerView(binding.list)
    }

    override fun initDatum() {
        super.initDatum()
        adapter = SongAdapter(R.layout.item_song, 1, supportFragmentManager)
        binding.list.adapter = adapter
        observeLocalMusicEvents()
        loadData()
    }

    override fun initListeners() {
        super.initListeners()
        adapter.setOnItemClickListener { _: BaseQuickAdapter<*, *>, _: View, position: Int ->
            if (adapter.isEditing()) {
                adapter.setSelected(position, !adapter.isSelected(position))
                showButtonStatus()
            } else {
                play(position)
            }
        }

        binding.select.setOnClickListener {
            selectClick()
        }

        binding.delete.setOnClickListener {
            deleteClick()
        }

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (adapter.isEditing()) {
                        exitEditMode()
                    } else {
                        isEnabled = false
                        onBackPressedDispatcher.onBackPressed()
                    }
                }
            },
        )
    }

    private fun selectClick() {
        val hasSelected = isSelected()
        for (i in 0 until adapter.itemCount) {
            adapter.setSelected(i, !hasSelected)
        }
        showButtonStatus()
    }

    private fun deleteClick() {
        val deleteIds = adapter.getSelectedIndexes()
            .mapNotNull { index -> adapter.getItemOrNull(index)?.id }
            .toSet()

        val iterator = adapter.data.iterator()
        while (iterator.hasNext()) {
            val song = iterator.next()
            if (song.id in deleteIds) {
                iterator.remove()
                orm.deleteSongById(song.id)
            }
        }

        adapter.notifyDataSetChanged()
        exitEditMode()
    }

    private fun isSelected(): Boolean {
        return adapter.getSelectedIndexes().isNotEmpty()
    }

    private fun showButtonStatus() {
        if (isSelected()) {
            binding.select.setText(R.string.cancel_select_all)
            binding.delete.isEnabled = true
        } else {
            defaultButtonStatus()
        }
    }

    private fun play(position: Int) {
        val data = adapter.getItem(position)
        musicListManager.datum = adapter.data
        musicListManager.play(data)
        startMusicPlayerActivity()
    }

    override fun loadData(isPlaceholder: Boolean) {
        super.loadData(isPlaceholder)
        val datum = orm.queryLocalMusic(sp.localMusicSortIndex)

        if (datum.size > 0) {
            adapter.setNewInstance(datum)
        } else {
            toScanLocalMusic()
        }
    }

    private fun toScanLocalMusic() {
        startActivity(ScanLocalMusicActivity::class.java)
    }

    private fun observeLocalMusicEvents() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                observeScanComplete().collect {
                    loadData()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_local_music, menu)
        editMenuItem = menu.findItem(R.id.edit)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.edit -> onEditClick()
            R.id.scan_local_music -> toScanLocalMusic()
            R.id.sort -> showSortDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onEditClick() {
        if (adapter.itemCount == 0) {
            SuperToast.show(R.string.no_local_music)
            return
        }

        if (adapter.isEditing()) {
            exitEditMode()
        } else {
            editMenuItem?.setTitle(R.string.cancel_edit)
            binding.controlContainer.visibility = View.VISIBLE
            adapter.setEditing(true)
        }
    }

    private fun exitEditMode() {
        editMenuItem?.setTitle(R.string.batch_edit)
        binding.controlContainer.visibility = View.GONE
        defaultButtonStatus()
        adapter.setEditing(false)
    }

    private fun defaultButtonStatus() {
        binding.select.setText(R.string.select_all)
        binding.delete.isEnabled = false
    }

    private fun showSortDialog() {
        MusicSortDialogFragment.show(
            supportFragmentManager,
            sp.localMusicSortIndex,
        ) { dialog, which ->
            dialog.dismiss()
            sp.localMusicSortIndex = which
            loadData()
        }
    }

}
