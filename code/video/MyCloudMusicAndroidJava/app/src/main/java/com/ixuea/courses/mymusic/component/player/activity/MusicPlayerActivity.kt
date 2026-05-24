package com.ixuea.courses.mymusic.component.player.activity

import android.graphics.RenderEffect
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.ixuea.android.downloader.domain.DownloadInfo
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.activity.BaseLogicActivity
import com.ixuea.courses.mymusic.component.download.domain.DownloadActionsUseCase
import com.ixuea.courses.mymusic.component.download.listener.MyDownloadListener
import com.ixuea.courses.mymusic.component.lyric.activity.SelectLyricActivity
import com.ixuea.courses.mymusic.component.lyric.model.Lyric
import com.ixuea.courses.mymusic.component.lyric.view.LyricListView
import com.ixuea.courses.mymusic.component.player.domain.ObserveMusicPlayListChangesUseCase
import com.ixuea.courses.mymusic.component.player.domain.ObserveRecordClicksUseCase
import com.ixuea.courses.mymusic.component.player.fragment.MusicPlayListDialogFragment
import com.ixuea.courses.mymusic.component.player.ui.MusicPlayerScreen
import com.ixuea.courses.mymusic.component.player.view.RecordPageView
import com.ixuea.courses.mymusic.component.song.model.Song
import com.ixuea.courses.mymusic.manager.MusicPlayerListener
import com.ixuea.courses.mymusic.manager.MusicPlayerManager
import com.ixuea.courses.mymusic.manager.model.MusicPlayListChange
import com.ixuea.courses.mymusic.playback.PlaybackService
import com.ixuea.courses.mymusic.ui.compose.MuseFlowTheme
import com.ixuea.courses.mymusic.util.FileUtil
import com.ixuea.courses.mymusic.util.PlayListUtil
import com.ixuea.courses.mymusic.util.ResourceUtil
import com.ixuea.courses.mymusic.util.StorageUtil
import com.ixuea.courses.mymusic.util.SwitchDrawableUtil
import com.ixuea.superui.toast.SuperToast
import com.qmuiteam.qmui.util.QMUIStatusBarHelper
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.coroutines.launch
import org.apache.commons.lang3.StringUtils
import timber.log.Timber
import java.lang.ref.SoftReference

/**
 * 黑胶唱片界面
 */
class MusicPlayerActivity :
    BaseLogicActivity(),
    MusicPlayerListener,
    LyricListView.LyricListListener {

    private lateinit var musicPlayerManager: MusicPlayerManager
    private lateinit var downloadActionsUseCase: DownloadActionsUseCase
    private var isSeekTracking = false
    private val observeRecordClicks = ObserveRecordClicksUseCase()
    private val observeMusicPlayListChanges = ObserveMusicPlayListChangesUseCase()
    private var backgroundView: ImageView? = null
    private var recordPageView: RecordPageView? = null
    private var registeredRecordPageView: RecordPageView? = null
    private var lyricListView: LyricListView? = null
    private var loadedBackgroundSongId: String? = null
    private var recordSongsSnapshot: List<Song> = emptyList()
    private var titleText by mutableStateOf("")
    private var subtitleText by mutableStateOf("")
    private var isPlaying by mutableStateOf(false)
    private var isLyricVisible by mutableStateOf(false)
    private var playbackProgress by mutableStateOf(0)
    private var playbackDuration by mutableStateOf(0)
    private var loopModel by mutableStateOf(0)
    private var downloadIcon by mutableStateOf(R.drawable.ic_download)
    private var lyricData by mutableStateOf<Lyric?>(null)

    /**
     * 下载任务
     */
    private var downloadInfo: DownloadInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        musicPlayerManager = PlaybackService.getMusicPlayerManager(applicationContext)
        downloadActionsUseCase = DownloadActionsUseCase()
        setContent {
            MuseFlowTheme {
                MusicPlayerScreen(
                    title = titleText.ifBlank { getString(R.string.activity_music_player) },
                    subtitle = subtitleText,
                    isPlaying = isPlaying,
                    isLyricVisible = isLyricVisible,
                    progress = playbackProgress,
                    duration = playbackDuration,
                    loopModel = loopModel,
                    downloadIcon = downloadIcon,
                    onBack = { onBackPressedDispatcher.onBackPressed() },
                    onDownloadClick = ::downloadClick,
                    onLoopClick = {
                        musicListManager.changeLoopModel()
                        showLoopModel()
                    },
                    onPreviousClick = ::playPrevious,
                    onPlayPauseClick = ::playOrPause,
                    onNextClick = ::playNext,
                    onListClick = {
                        MusicPlayListDialogFragment.show(supportFragmentManager)
                    },
                    onSeekChange = { value ->
                        isSeekTracking = true
                        playbackProgress = value
                        musicListManager.seekTo(value)
                    },
                    onSeekFinished = {
                        isSeekTracking = false
                    },
                    onBackgroundReady = ::bindBackgroundView,
                    onRecordReady = ::bindRecordPageView,
                    onLyricReady = ::bindLyricListView,
                )
            }
        }
    }

    override fun initViews() {
        super.initViews()
        QMUIStatusBarHelper.setStatusBarDarkMode(this)
        QMUIStatusBarHelper.translucent(this)
    }

    /**
     * 歌曲滚动监听器
     */
    private val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrollStateChanged(state: Int) {
            super.onPageScrollStateChanged(state)
            when (state) {
                ViewPager2.SCROLL_STATE_DRAGGING -> {
                    currentRecordSong()?.let { currentSong ->
                        recordRotate(currentSong, false)
                    }
                }

                ViewPager2.SCROLL_STATE_IDLE -> {
                    val song = currentRecordSong() ?: return
                    val currentSong = musicListManager.data
                    if (currentSong?.id == song.id) {
                        if (musicPlayerManager.isPlaying) {
                            recordRotate(song, true)
                        }
                    } else {
                        musicListManager.play(song)
                    }
                }
            }
        }
    }

    fun currentRecordSong(): Song? {
        val songs = musicListManager.datum
        val currentItem = recordPageView?.list?.currentItem ?: 0
        return songs.getOrNull(currentItem)
    }

    fun showLyricList() {
        isLyricVisible = true
    }

    fun handleMusicPlayListChange(event: MusicPlayListChange) {
        recordPageView?.adapter?.let { adapter ->
            if (event.isDeleteAll) {
                adapter.removeAll()
            } else if (event.position in 0 until adapter.itemCount) {
                adapter.remove(event.position)
            }
        }

        if (musicListManager.datum.isEmpty()) {
            finish()
        }
    }

    fun recordRotate(data: Song, isRotate: Boolean) {
        data.isRotate = isRotate
        recordPageView?.setPlaying(isRotate)
    }

    override fun initDatum() {
        super.initDatum()
        if (!::musicPlayerManager.isInitialized) {
            musicPlayerManager = PlaybackService.getMusicPlayerManager(applicationContext)
        }
        if (!::downloadActionsUseCase.isInitialized) {
            downloadActionsUseCase = DownloadActionsUseCase()
        }

        observePlayerEvents()
    }

    private fun observePlayerEvents() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    observeRecordClicks().collect {
                        showLyricList()
                    }
                }

                launch {
                    observeMusicPlayListChanges().collect(::handleMusicPlayListChange)
                }
            }
        }
    }

    fun downloadClick() {
        val info = downloadInfo
        if (info != null) {
            if (info.status == DownloadInfo.STATUS_COMPLETED) {
                SuperToast.show(R.string.already_downloaded)
            } else {
                SuperToast.success(R.string.already_in_download_list)

                if (info.status != DownloadInfo.STATUS_DOWNLOADING &&
                    info.status != DownloadInfo.STATUS_WAIT
                ) {
                    downloadActionsUseCase.resume(info)
                }
            }
        } else {
            createDownload()
        }
    }

    fun playPrevious() {
        val data = resolveAdjacentSong(isPrevious = true) ?: return
        musicListManager.play(data)
    }

    fun playNext() {
        val data = resolveAdjacentSong(isPrevious = false) ?: return
        musicListManager.play(data)
    }

    fun resolveAdjacentSong(isPrevious: Boolean): Song? {
        val songs = musicListManager.datum
        if (songs.isEmpty()) {
            return null
        }

        val current = musicListManager.data
        if (current == null || current !in songs) {
            return songs.first()
        }

        return runCatching {
            if (isPrevious) {
                musicListManager.previous()
            } else {
                musicListManager.next()
            }
        }.getOrNull()
    }

    /**
     * 创建下载任务
     */
    fun createDownload() {
        val data = currentSong() ?: return
        val urlString = ResourceUtil.resourceUri(data.uri)
        val path = StorageUtil.getExternalPath(
            hostActivity,
            sp.userId,
            data.title,
            StorageUtil.MP3
        ).absolutePath

        Timber.d("createDownload %s", path)

        downloadInfo = DownloadInfo.Builder()
            .setId(data.id)
            .setUrl(urlString)
            .setPath(path)
            .build()
            .apply {
                setCreateAt(System.currentTimeMillis())
            }

        setDownloadCallback()
        downloadInfo?.let { info ->
            downloadActionsUseCase.download(info)
        }

        orm.saveSong(data)
        SuperToast.success(R.string.add_success)
    }

    /**
     * 设置下载回调
     */
    private fun setDownloadCallback() {
        downloadInfo?.setDownloadListener(
            object : MyDownloadListener(SoftReference<Any>(this)) {
                override fun onRefresh() {
                    val viewHolder = userTag?.get() as? MusicPlayerActivity ?: return
                    viewHolder.refresh()
                }
            }
        )
    }

    fun refresh() {
        val info = downloadInfo
        if (info != null) {
            when (info.status) {
                DownloadInfo.STATUS_COMPLETED -> {
                    downloadIcon = R.drawable.ic_downloaded
                }

                else -> normalDownloadStatusUI()
            }

            val start = FileUtil.formatFileSize(info.progress)
            val size = FileUtil.formatFileSize(info.size)
            Timber.d("download music refresh %s %s", start, size)
        } else {
            normalDownloadStatusUI()
        }
    }

    /**
     * 未下载状态
     */
    private fun normalDownloadStatusUI() {
        downloadIcon = R.drawable.ic_download
    }

    fun showLoopModel() {
        loopModel = musicListManager.loopModel
    }

    /**
     * 播放或暂停
     */
    fun playOrPause() {
        if (musicPlayerManager.isPlaying) {
            musicListManager.pause()
        } else {
            musicListManager.resume()
        }
    }

    /**
     * 选中当前音乐
     */
    private fun scrollPosition() {
        val index = musicListManager.datum.indexOf(currentSong())
        recordPageView?.scrollPosition(index)
    }

    private fun showLyricData() {
        lyricData = currentSong()?.parsedLyric
        lyricListView?.setData(lyricData)
    }

    override fun onResume() {
        super.onResume()
        showInitData()
        showDuration()
        showProgress()
        showMusicPlayStatus()
        showLoopModel()
        scrollPosition()
        showLyricData()

        musicPlayerManager.addMusicPlayerListener(this)
    }

    /**
     * 界面不可见了
     */
    override fun onPause() {
        super.onPause()
        musicPlayerManager.removeMusicPlayerListener(this)
    }

    override fun onDestroy() {
        registeredRecordPageView?.list?.unregisterOnPageChangeCallback(pageChangeCallback)
        registeredRecordPageView = null
        super.onDestroy()
    }

    /**
     * 显示播放状态
     */
    private fun showPlayStatus() {
        isPlaying = false
        recordPageView?.setPlaying(false)
    }

    /**
     * 显示暂停状态
     */
    private fun showPauseStatus() {
        isPlaying = true
        recordPageView?.setPlaying(true)
    }

    /**
     * 显示音乐播放状态
     */
    private fun showMusicPlayStatus() {
        if (musicPlayerManager.isPlaying) {
            showPauseStatus()
        } else {
            showPlayStatus()
        }
    }

    /**
     * 显示播放进度
     */
    private fun showProgress() {
        val progress = currentSong()?.progress?.toInt() ?: 0

        playbackProgress = progress
        lyricListView?.setProgress(progress)
    }

    /**
     * 显示时长
     */
    private fun showDuration() {
        val end = currentSong()?.duration?.toInt() ?: 0

        playbackDuration = end
    }

    /**
     * 显示初始化数据
     */
    private fun showInitData() {
        val data = currentSong()
        if (data == null) {
            finish()
            return
        }

        titleText = data.title.orEmpty()
        subtitleText = data.singer?.nickname.orEmpty()
        loadBackground(data)

        downloadInfo = data.id?.let { songId -> downloadActionsUseCase.getDownloadById(songId) }
        if (downloadInfo != null) {
            setDownloadCallback()
        }

        refresh()
    }

    private fun loadBackground(data: Song) {
        val targetView = backgroundView ?: return
        loadedBackgroundSongId = data.id
        val icon = data.icon.orEmpty()
        val requestBuilder: RequestBuilder<Drawable> = Glide.with(this).asDrawable()
        if (StringUtils.isBlank(icon)) {
            requestBuilder.load(R.drawable.default_cover)
        } else if (icon.isDirectImageUri()) {
            requestBuilder.load(icon)
        } else {
            requestBuilder.load(ResourceUtil.resourceUri(icon))
        }

        val backgroundRequest = if (targetView.applyPlatformBlurIfSupported()) {
            requestBuilder
        } else {
            requestBuilder.apply(bitmapTransform(BlurTransformation(BITMAP_BLUR_RADIUS, BITMAP_BLUR_SAMPLING)))
        }

        backgroundRequest
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    val switchDrawableUtil = SwitchDrawableUtil(
                        targetView.drawable,
                        resource
                    )
                    targetView.setImageDrawable(switchDrawableUtil.drawable)
                    switchDrawableUtil.start()
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
    }

    private fun ImageView.applyPlatformBlurIfSupported(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            return false
        }

        val radiusPx = resources.displayMetrics.density * PLATFORM_BLUR_RADIUS_DP
        setRenderEffect(
            RenderEffect.createBlurEffect(
                radiusPx,
                radiusPx,
                Shader.TileMode.CLAMP
            )
        )
        return true
    }

    private fun String.isDirectImageUri(): Boolean {
        return startsWith("http://") ||
            startsWith("https://") ||
            startsWith("android.resource://") ||
            startsWith("content://") ||
            startsWith("file://")
    }

    private fun currentSong(): Song? {
        return musicListManager.data ?: musicListManager.datum.firstOrNull()
    }

    override fun onLyricReady(data: Song) {
        showLyricData()
    }

    override fun onPaused(data: Song) {
        showPlayStatus()
    }

    override fun onPlaying(data: Song) {
        showPauseStatus()
    }

    override fun onPrepared(mp: MediaPlayer?, data: Song) {
        showInitData()
        showDuration()
        scrollPosition()
    }

    override fun onProgress(data: Song) {
        if (isSeekTracking) {
            return
        }

        showProgress()
    }

    override fun onLyricClick() {
        isLyricVisible = false
    }

    override fun onLyricLongClick(): Boolean {
        val data = currentSong() ?: return false
        startActivityExtraData(SelectLyricActivity::class.java, data)
        return true
    }

    private fun bindBackgroundView(view: ImageView) {
        backgroundView = view
        currentSong()
            ?.takeIf { song -> loadedBackgroundSongId != song.id }
            ?.let(::loadBackground)
    }

    private fun bindRecordPageView(view: RecordPageView) {
        if (recordPageView !== view) {
            recordPageView = view
        }

        if (view.adapter == null) {
            view.initAdapter(hostActivity)
        }

        val songs = musicListManager.datum.toList()
        if (recordSongsSnapshot != songs) {
            view.setData(songs)
            recordSongsSnapshot = songs
        }

        if (registeredRecordPageView !== view) {
            registeredRecordPageView?.list?.unregisterOnPageChangeCallback(pageChangeCallback)
            view.list.registerOnPageChangeCallback(pageChangeCallback)
            registeredRecordPageView = view
        }
    }

    private fun bindLyricListView(view: LyricListView) {
        lyricListView = view
        view.setLyricListListener(this)
        view.setData(lyricData)
        view.setProgress(playbackProgress)
    }

    companion object {
        private const val BITMAP_BLUR_RADIUS = 25
        private const val BITMAP_BLUR_SAMPLING = 6
        private const val PLATFORM_BLUR_RADIUS_DP = 22F
    }
}
