package com.ixuea.courses.mymusic.component.music.activity

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.activity.BaseLogicActivity
import com.ixuea.courses.mymusic.component.download.domain.DownloadActionsUseCase
import com.ixuea.courses.mymusic.component.music.domain.ObserveLocalMusicScanCompleteUseCase
import com.ixuea.courses.mymusic.component.music.fragment.MusicSortDialogFragment
import com.ixuea.courses.mymusic.component.music.ui.LocalMusicScreen
import com.ixuea.courses.mymusic.component.song.model.Song
import com.ixuea.courses.mymusic.ui.compose.MuseFlowTheme
import com.ixuea.superui.dialog.SuperDialog
import com.ixuea.superui.toast.SuperToast
import kotlinx.coroutines.launch

/**
 * 本地音乐界面
 */
class LocalMusicActivity : BaseLogicActivity() {
    private var songs by mutableStateOf<List<Song>>(emptyList())
    private var editing by mutableStateOf(false)
    private var selectedIndexes by mutableStateOf<Set<Int>>(emptySet())
    private val observeScanComplete = ObserveLocalMusicScanCompleteUseCase()
    private val downloadActionsUseCase = DownloadActionsUseCase()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MuseFlowTheme {
                LocalMusicScreen(
                    songs = songs,
                    editing = editing,
                    selectedIndexes = selectedIndexes,
                    onBack = { onBackPressedDispatcher.onBackPressed() },
                    onEditClick = ::onEditClick,
                    onScanClick = ::toScanLocalMusic,
                    onSortClick = ::showSortDialog,
                    onSongClick = ::onSongClick,
                    onDeleteOneClick = ::confirmDeleteSong,
                    onSelectAllClick = ::selectClick,
                    onDeleteSelectedClick = ::deleteClick,
                )
            }
        }
    }

    override fun initDatum() {
        super.initDatum()
        observeLocalMusicEvents()
        loadData()
    }

    override fun initListeners() {
        super.initListeners()
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (editing) {
                        exitEditMode()
                    } else {
                        isEnabled = false
                        onBackPressedDispatcher.onBackPressed()
                    }
                }
            },
        )
    }

    fun selectClick() {
        selectedIndexes = if (selectedIndexes.isNotEmpty()) {
            emptySet()
        } else {
            songs.indices.toSet()
        }
    }

    fun deleteClick() {
        val deleteIds = selectedIndexes
            .mapNotNull { index -> songs.getOrNull(index)?.id }
            .toSet()
        if (deleteIds.isEmpty()) {
            return
        }

        songs.forEach { song ->
            if (song.id in deleteIds) {
                orm.deleteSongById(song.id)
            }
        }
        songs = songs.filterNot { it.id in deleteIds }
        exitEditMode()
    }

    fun onSongClick(position: Int) {
        if (editing) {
            selectedIndexes = if (position in selectedIndexes) {
                selectedIndexes - position
            } else {
                selectedIndexes + position
            }
            return
        }

        play(position)
    }

    fun play(position: Int) {
        val data = songs.getOrNull(position) ?: return
        musicListManager.datum = songs
        musicListManager.play(data)
        startMusicPlayerActivity()
    }

    override fun loadData(isPlaceholder: Boolean) {
        super.loadData(isPlaceholder)
        val datum = orm.queryLocalMusic(sp.localMusicSortIndex)

        if (datum.size > 0) {
            songs = datum
        } else {
            songs = emptyList()
            toScanLocalMusic()
        }
        selectedIndexes = emptySet()
    }

    fun toScanLocalMusic() {
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

    fun onEditClick() {
        if (songs.isEmpty()) {
            SuperToast.show(R.string.no_local_music)
            return
        }

        if (editing) {
            exitEditMode()
        } else {
            editing = true
            selectedIndexes = emptySet()
        }
    }

    fun exitEditMode() {
        editing = false
        selectedIndexes = emptySet()
    }

    fun confirmDeleteSong(song: Song) {
        SuperDialog.newInstance(supportFragmentManager)
            .setTitleRes(R.string.confirm_delete)
            .setOnClickListener {
                deleteSong(song)
            }
            .show()
    }

    private fun deleteSong(song: Song) {
        val downloadInfo = song.id?.let { songId ->
            downloadActionsUseCase.getDownloadById(songId)
        }
        if (downloadInfo != null) {
            downloadActionsUseCase.remove(downloadInfo)
        } else {
            orm.deleteSong(song)
        }

        songs = songs.filterNot { it.id == song.id }
        selectedIndexes = emptySet()
    }

    fun showSortDialog() {
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
