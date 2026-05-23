package com.ixuea.courses.mymusic.component.sheet.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.ixuea.android.downloader.domain.DownloadInfo
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.activity.BaseLogicActivity
import com.ixuea.courses.mymusic.component.comment.activity.CommentActivity
import com.ixuea.courses.mymusic.component.download.domain.DownloadActionsUseCase
import com.ixuea.courses.mymusic.component.login.activity.LoginHomeActivity
import com.ixuea.courses.mymusic.component.sheet.domain.NotifySheetChangedUseCase
import com.ixuea.courses.mymusic.component.sheet.model.Sheet
import com.ixuea.courses.mymusic.component.sheet.ui.SheetCollectOperation
import com.ixuea.courses.mymusic.component.sheet.ui.SheetDetailScreen
import com.ixuea.courses.mymusic.component.sheet.ui.SheetDetailUiState
import com.ixuea.courses.mymusic.component.sheet.ui.SheetDetailViewModel
import com.ixuea.courses.mymusic.component.sheet.ui.SmallAudioControlHost
import com.ixuea.courses.mymusic.component.song.model.Song
import com.ixuea.courses.mymusic.component.user.activity.UserDetailActivity
import com.ixuea.courses.mymusic.ui.compose.MuseFlowTheme
import com.ixuea.courses.mymusic.util.Constant
import com.ixuea.courses.mymusic.util.PreferenceUtil
import com.ixuea.superui.toast.SuperToast
import timber.log.Timber

/**
 * 歌单详情界面
 */
class SheetDetailActivity : BaseLogicActivity() {
    private var id: String? = null
    private lateinit var viewModel: SheetDetailViewModel
    private val notifySheetChangedUseCase = NotifySheetChangedUseCase()
    private val downloadActionsUseCase = DownloadActionsUseCase()
    private var handledDataVersion = 0L
    private var handledErrorVersion = 0L
    private var handledCollectEventVersion = 0L
    private var loadingVisible = false
    private var headerColor by mutableStateOf(0)
    private var canDelete by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sp = PreferenceUtil.getInstance(this)
        id = if (Intent.ACTION_VIEW == intent.action) {
            intent.data?.getQueryParameter(Constant.ID)
        } else {
            extraId()
        }
        viewModel = ViewModelProvider(this)[SheetDetailViewModel::class.java]
        headerColor = ContextCompat.getColor(this, R.color.primary)
        setStatusBarColor(headerColor)

        setContent {
            val state by viewModel.uiState.collectAsState()

            LaunchedEffect(state.isLoading, state.isCollecting) {
                renderLoading(state.isLoading || state.isCollecting)
            }
            LaunchedEffect(state.dataVersion) {
                renderData(state)
            }
            LaunchedEffect(state.collectEventVersion) {
                renderCollectEvent(state.collectOperation, state.collectEventVersion)
            }
            LaunchedEffect(state.errorVersion) {
                renderError(state)
            }

            MuseFlowTheme {
                SheetDetailScreen(
                    state = state,
                    headerColor = headerColor,
                    canDelete = canDelete,
                    isDownloaded = ::isDownloaded,
                    onBack = { onBackPressedDispatcher.onBackPressed() },
                    onHeaderColorResolved = ::applyHeaderColor,
                    onPlayAll = {
                        play(0, state.sheet?.songs.orEmpty())
                    },
                    onSongClick = { position ->
                        play(position, state.sheet?.songs.orEmpty())
                    },
                    onCollectClick = ::processCollectClick,
                    onUserClick = { userId ->
                        startActivityExtraId(UserDetailActivity::class.java, userId)
                    },
                    onCommentClick = { sheetId ->
                        CommentActivity.startWithSheetId(hostActivity, sheetId)
                    },
                    onSearchClick = {},
                    onSortClick = {},
                    onDeleteClick = ::deleteSheet,
                    onReportClick = {},
                    bottomBar = {
                        SmallAudioControlHost(supportFragmentManager)
                    },
                )
            }
        }
    }

    override fun initDatum() {
        super.initDatum()
        loadData()
    }

    override fun loadData(isPlaceholder: Boolean) {
        super.loadData(isPlaceholder)
        viewModel.load(id.orEmpty())
    }

    private fun renderLoading(show: Boolean) {
        if (show == loadingVisible) {
            return
        }

        loadingVisible = show
        if (show) {
            showLoading()
        } else {
            hideLoading()
        }
    }

    private fun renderData(state: SheetDetailUiState) {
        if (state.dataVersion == handledDataVersion) {
            return
        }

        handledDataVersion = state.dataVersion
        val ownerId = state.sheet?.user?.id
        canDelete = ownerId != null && ownerId == sp.userId
    }

    private fun renderCollectEvent(operation: SheetCollectOperation, version: Long) {
        if (version == handledCollectEventVersion) {
            return
        }

        handledCollectEventVersion = version
        when (operation) {
            SheetCollectOperation.COLLECTED -> SuperToast.success(R.string.collection_success)
            SheetCollectOperation.UNCOLLECTED -> SuperToast.success(R.string.cancel_success)
            SheetCollectOperation.NONE -> return
        }

        notifySheetChanged()
    }

    private fun renderError(state: SheetDetailUiState) {
        if (state.errorVersion == handledErrorVersion) {
            return
        }

        handledErrorVersion = state.errorVersion
        if (state.error != null) {
            Timber.e(state.error, "sheet detail error %s", state.errorMessage)
        } else {
            Timber.e("sheet detail error %s", state.errorMessage)
        }
        state.errorMessage?.takeIf { it.isNotBlank() }?.let(SuperToast::show)
    }

    private fun applyHeaderColor(color: Int) {
        if (color == headerColor) {
            return
        }

        headerColor = color
        setStatusBarColor(color)
    }

    private fun play(position: Int, songs: List<Song>) {
        if (position !in songs.indices) {
            return
        }

        val song = songs[position]
        musicListManager.datum = songs
        musicListManager.play(song)
        startMusicPlayerActivity()
    }

    private fun processCollectClick(sheet: Sheet) {
        if (!sp.isLogin) {
            startActivity(LoginHomeActivity::class.java)
            return
        }

        viewModel.toggleCollect(sheet)
    }

    private fun isDownloaded(song: Song): Boolean {
        val downloadInfo = song.id?.let { songId ->
            downloadActionsUseCase.getDownloadById(songId)
        }
        return downloadInfo?.status == DownloadInfo.STATUS_COMPLETED
    }

    private fun deleteSheet() {
    }

    private fun notifySheetChanged() {
        notifySheetChangedUseCase()
    }

    companion object {
        fun start(context: android.content.Context, id: String?) {
            val intent = Intent(context, SheetDetailActivity::class.java)
            intent.putExtra(Constant.ID, id)
            context.startActivity(intent)
        }
    }
}
