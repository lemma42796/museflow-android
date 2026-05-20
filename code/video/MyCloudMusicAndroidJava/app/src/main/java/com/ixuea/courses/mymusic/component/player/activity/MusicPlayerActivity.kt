package com.ixuea.courses.mymusic.component.player.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.view.View
import android.widget.SeekBar
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.ixuea.android.downloader.domain.DownloadInfo
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.activity.BaseTitleActivity
import com.ixuea.courses.mymusic.component.download.domain.DownloadActionsUseCase
import com.ixuea.courses.mymusic.component.download.listener.MyDownloadListener
import com.ixuea.courses.mymusic.component.lyric.activity.SelectLyricActivity
import com.ixuea.courses.mymusic.component.lyric.view.LyricListView
import com.ixuea.courses.mymusic.component.player.fragment.MusicPlayListDialogFragment
import com.ixuea.courses.mymusic.component.player.model.event.RecordClickEvent
import com.ixuea.courses.mymusic.component.song.model.Song
import com.ixuea.courses.mymusic.databinding.ActivityMusicPlayerBinding
import com.ixuea.courses.mymusic.manager.MusicPlayerListener
import com.ixuea.courses.mymusic.manager.MusicPlayerManager
import com.ixuea.courses.mymusic.manager.model.event.MusicPlayListChangedEvent
import com.ixuea.courses.mymusic.playback.PlaybackService
import com.ixuea.courses.mymusic.util.FileUtil
import com.ixuea.courses.mymusic.util.PlayListUtil
import com.ixuea.courses.mymusic.util.ResourceUtil
import com.ixuea.courses.mymusic.util.StorageUtil
import com.ixuea.courses.mymusic.util.SuperDateUtil
import com.ixuea.courses.mymusic.util.SwitchDrawableUtil
import com.ixuea.superui.toast.SuperToast
import com.qmuiteam.qmui.util.QMUIStatusBarHelper
import jp.wasabeef.glide.transformations.BlurTransformation
import org.apache.commons.lang3.StringUtils
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import java.lang.ref.SoftReference

/**
 * 黑胶唱片界面
 */
class MusicPlayerActivity :
    BaseTitleActivity<ActivityMusicPlayerBinding>(),
    MusicPlayerListener,
    SeekBar.OnSeekBarChangeListener,
    LyricListView.LyricListListener {

    private lateinit var musicPlayerManager: MusicPlayerManager
    private lateinit var downloadActionsUseCase: DownloadActionsUseCase
    private var isSeekTracking = false

    /**
     * 下载任务
     */
    private var downloadInfo: DownloadInfo? = null

    override fun isRegisterEventBus(): Boolean {
        return true
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

    private fun currentRecordSong(): Song? {
        val songs = musicListManager.datum
        val currentItem = binding.record.binding.list.currentItem
        return songs.getOrNull(currentItem)
    }

    /**
     * 黑胶唱片点击事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    @Suppress("UNUSED_PARAMETER")
    fun onRecordClickEvent(event: RecordClickEvent) {
        binding.lyricList.alpha = 0F
        binding.lyricList.visibility = View.VISIBLE

        ValueAnimator.ofFloat(0F, 1F).apply {
            addUpdateListener { animation ->
                val value = animation.animatedValue as Float
                binding.lyricList.alpha = value
                binding.record.alpha = 1F - value
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    binding.record.visibility = View.GONE
                }
            })
            duration = ANIMATION_DURATION
            start()
        }
    }

    /**
     * 音乐播放列表改变了事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun musicPlayListChangedEvent(event: MusicPlayListChangedEvent) {
        binding.record.adapter?.let { adapter ->
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

    private fun recordRotate(data: Song, isRotate: Boolean) {
        data.isRotate = isRotate
        binding.record.setPlaying(isRotate)
    }

    override fun initDatum() {
        super.initDatum()
        musicPlayerManager = PlaybackService.getMusicPlayerManager(applicationContext)
        downloadActionsUseCase = DownloadActionsUseCase()

        binding.record.initAdapter(hostActivity)
        binding.record.setData(musicListManager.datum)
    }

    override fun initListeners() {
        super.initListeners()
        binding.record.binding.list.registerOnPageChangeCallback(pageChangeCallback)

        binding.download.setOnClickListener {
            downloadClick()
        }

        binding.progress.setOnSeekBarChangeListener(this)

        binding.loopModel.setOnClickListener {
            musicListManager.changeLoopModel()
            showLoopModel()
        }

        binding.previous.setOnClickListener {
            playPrevious()
        }

        binding.play.setOnClickListener {
            playOrPause()
        }

        binding.next.setOnClickListener {
            playNext()
        }

        binding.listButton.setOnClickListener {
            MusicPlayListDialogFragment.show(supportFragmentManager)
        }

        binding.lyricList.setLyricListListener(this)
    }

    private fun downloadClick() {
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

    private fun playPrevious() {
        val data = resolveAdjacentSong(isPrevious = true) ?: return
        musicListManager.play(data)
    }

    private fun playNext() {
        val data = resolveAdjacentSong(isPrevious = false) ?: return
        musicListManager.play(data)
    }

    private fun resolveAdjacentSong(isPrevious: Boolean): Song? {
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
    private fun createDownload() {
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

    private fun refresh() {
        val info = downloadInfo
        if (info != null) {
            when (info.status) {
                DownloadInfo.STATUS_COMPLETED -> {
                    binding.download.setImageResource(R.drawable.ic_downloaded)
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
        binding.download.setImageResource(R.drawable.ic_download)
    }

    private fun showLoopModel() {
        binding.loopModel.setImageResource(
            PlayListUtil.getLoopModelIcon(musicListManager.loopModel)
        )
    }

    /**
     * 播放或暂停
     */
    private fun playOrPause() {
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
        binding.record.scrollPosition(index)
    }

    private fun showLyricData() {
        binding.lyricList.setData(currentSong()?.parsedLyric)
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
        binding.record.binding.list.unregisterOnPageChangeCallback(pageChangeCallback)
        super.onDestroy()
    }

    /**
     * 显示播放状态
     */
    private fun showPlayStatus() {
        binding.play.setImageResource(R.drawable.music_play)
        binding.record.setPlaying(false)
    }

    /**
     * 显示暂停状态
     */
    private fun showPauseStatus() {
        binding.play.setImageResource(R.drawable.music_pause)
        binding.record.setPlaying(true)
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

        binding.start.text = SuperDateUtil.ms2ms(progress)
        binding.progress.progress = progress
        binding.lyricList.setProgress(progress)
    }

    /**
     * 显示时长
     */
    private fun showDuration() {
        val end = currentSong()?.duration?.toInt() ?: 0

        binding.end.text = SuperDateUtil.ms2ms(end)
        binding.progress.max = end
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

        title = data.title
        toolbar.subtitle = data.singer?.nickname.orEmpty()
        loadBackground(data)

        downloadInfo = data.id?.let { songId -> downloadActionsUseCase.getDownloadById(songId) }
        if (downloadInfo != null) {
            setDownloadCallback()
        }

        refresh()
    }

    private fun loadBackground(data: Song) {
        val requestBuilder: RequestBuilder<Drawable> = Glide.with(this).asDrawable()
        if (StringUtils.isBlank(data.icon)) {
            requestBuilder.load(R.drawable.default_cover)
        } else {
            requestBuilder.load(ResourceUtil.resourceUri(data.icon))
        }

        val options = bitmapTransform(BlurTransformation(25, 3))
        requestBuilder
            .apply(options)
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    val switchDrawableUtil = SwitchDrawableUtil(
                        binding.background.drawable,
                        resource
                    )
                    binding.background.setImageDrawable(switchDrawableUtil.drawable)
                    switchDrawableUtil.start()
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
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

    /**
     * 进度条改变了
     */
    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        if (fromUser) {
            musicListManager.seekTo(progress)
        }
    }

    /**
     * 开始拖拽进度条
     */
    override fun onStartTrackingTouch(seekBar: SeekBar) {
        isSeekTracking = true
    }

    /**
     * 停止拖拽进度条
     */
    override fun onStopTrackingTouch(seekBar: SeekBar) {
        isSeekTracking = false
    }

    override fun onLyricClick() {
        binding.record.alpha = 0F
        binding.record.visibility = View.VISIBLE

        ValueAnimator.ofFloat(0F, 1F).apply {
            addUpdateListener { animation ->
                val value = animation.animatedValue as Float
                binding.record.alpha = value
                binding.lyricList.alpha = 1F - value
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    binding.lyricList.visibility = View.GONE
                }
            })
            duration = ANIMATION_DURATION
            start()
        }
    }

    override fun onLyricLongClick(): Boolean {
        val data = currentSong() ?: return false
        startActivityExtraData(SelectLyricActivity::class.java, data)
        return true
    }

    companion object {
        private const val ANIMATION_DURATION = 300L
    }
}
