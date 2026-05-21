package com.ixuea.courses.mymusic.manager.impl

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.os.Build
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.ixuea.courses.mymusic.MainActivity
import com.ixuea.courses.mymusic.component.lyric.view.GlobalLyricView
import com.ixuea.courses.mymusic.component.song.model.Song
import com.ixuea.courses.mymusic.manager.GlobalLyricManager
import com.ixuea.courses.mymusic.manager.MusicListManager
import com.ixuea.courses.mymusic.manager.MusicPlayerListener
import com.ixuea.courses.mymusic.manager.MusicPlayerManager
import com.ixuea.courses.mymusic.playback.PlaybackService
import com.ixuea.courses.mymusic.util.Constant
import com.ixuea.courses.mymusic.util.NotificationUtil
import com.ixuea.courses.mymusic.util.PreferenceUtil
import com.ixuea.courses.mymusic.util.ScreenUtil
import com.ixuea.courses.mymusic.util.SizeUtil
import com.ixuea.courses.mymusic.util.WidgetUtil

/**
 * 全局（桌面）歌词管理器实现。
 */
class GlobalLyricManagerImpl private constructor(context: Context) :
    GlobalLyricManager,
    MusicPlayerListener,
    GlobalLyricView.OnGlobalLyricDragListener,
    GlobalLyricView.GlobalLyricListener {

    private val context: Context = context.applicationContext
    private val sp: PreferenceUtil = PreferenceUtil.getInstance(this.context)
    private val musicPlayerManager: MusicPlayerManager = PlaybackService.getMusicPlayerManager(this.context)
    private lateinit var windowManager: WindowManager
    private lateinit var layoutParams: WindowManager.LayoutParams
    private var globalLyricView: GlobalLyricView? = null
    private var unlockGlobalLyricBroadcastReceiver: BroadcastReceiver? = null

    init {
        musicPlayerManager.addMusicPlayerListener(this)
        initWindowManager()

        if (sp.isShowGlobalLyric) {
            initGlobalLyricView()

            if (sp.isGlobalLyricLock) {
                lock()
            }
        }
    }

    private fun lock() {
        sp.isGlobalLyricLock = true
        setGlobalLyricStatus()
        globalLyricView?.simpleStyle()
        updateView()
        NotificationUtil.showUnlockGlobalLyricNotification(context)
        registerUnlockGlobalLyricReceiver()
    }

    private fun registerUnlockGlobalLyricReceiver() {
        if (unlockGlobalLyricBroadcastReceiver != null) {
            return
        }

        unlockGlobalLyricBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (Constant.ACTION_UNLOCK_LYRIC == intent.action) {
                    unlock()
                }
            }
        }

        val intentFilter = IntentFilter().apply {
            addAction(Constant.ACTION_UNLOCK_LYRIC)
        }
        context.registerReceiver(unlockGlobalLyricBroadcastReceiver, intentFilter)
    }

    private fun unlock() {
        sp.isGlobalLyricLock = false
        setGlobalLyricStatus()
        globalLyricView?.normalStyle()
        updateView()
        NotificationUtil.clearUnlockGlobalLyricNotification(context)
        unregisterUnlockGlobalLyricReceiver()
    }

    private fun unregisterUnlockGlobalLyricReceiver() {
        val receiver = unlockGlobalLyricBroadcastReceiver ?: return
        context.unregisterReceiver(receiver)
        unlockGlobalLyricBroadcastReceiver = null
    }

    override fun show() {
        if (!Settings.canDrawOverlays(context)) {
            val intent = Intent(context, MainActivity::class.java).apply {
                action = Constant.ACTION_LYRIC
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            return
        }

        initGlobalLyricView()
        sp.isShowGlobalLyric = true
        WidgetUtil.onGlobalLyricShowStatusChanged(context, isShowing())
    }

    override fun hide() {
        val view = globalLyricView
        if (view != null) {
            windowManager.removeView(view)
            globalLyricView = null
        }

        sp.isShowGlobalLyric = false
        WidgetUtil.onGlobalLyricShowStatusChanged(context, isShowing())
    }

    override fun isShowing(): Boolean = globalLyricView != null

    private fun initWindowManager() {
        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        layoutParams = WindowManager.LayoutParams().apply {
            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
            }
            format = PixelFormat.RGBA_8888
            gravity = Gravity.CENTER_HORIZONTAL or Gravity.TOP
            width = ScreenUtil.getScreenWith(context)
            height = WindowManager.LayoutParams.WRAP_CONTENT
            y = sp.globalLyricViewY
        }
        setGlobalLyricStatus()
    }

    private fun setGlobalLyricStatus() {
        layoutParams.flags = if (sp.isGlobalLyricLock) {
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        } else {
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        }
    }

    private fun initGlobalLyricView() {
        var view = globalLyricView
        if (view == null) {
            view = GlobalLyricView(context).apply {
                setLyricDragListener(this@GlobalLyricManagerImpl)
                setGlobalLyricListener(this@GlobalLyricManagerImpl)
                setGlobalLyricOtherListener(object : GlobalLyricView.GlobalLyricOtherListener {
                    override fun closeLyric() {
                        hide()
                    }
                })
            }
            globalLyricView = view
        }

        if (view.parent == null) {
            windowManager.addView(view, layoutParams)
        }

        showMusicPlayStatus()
        showLyricData()
    }

    private fun showMusicPlayStatus() {
        globalLyricView?.setPlay(musicPlayerManager.isPlaying)
    }

    private fun showLyricData() {
        val view = globalLyricView ?: return
        val data = musicListManager.data
        val lyric = data?.parsedLyric
        if (lyric == null) {
            view.clearLyric()
            return
        }

        view.setAccurate(lyric.isAccurate)
        onProgress(data)
    }

    protected val musicListManager: MusicListManager
        get() = PlaybackService.getListManager(context)

    override fun onPaused(data: Song?) {
        if (hasGlobalLyricView()) {
            globalLyricView?.setPlay(false)
        }
    }

    override fun onPlaying(data: Song?) {
        if (hasGlobalLyricView()) {
            globalLyricView?.setPlay(true)
        }
    }

    override fun onProgress(data: Song?) {
        if (data?.parsedLyric == null || !hasGlobalLyricView()) {
            return
        }

        globalLyricView?.onProgress(data)
    }

    override fun onLyricReady(data: Song?) {
        showLyricData()
    }

    private fun hasGlobalLyricView(): Boolean = globalLyricView != null

    override fun onGlobalLyricDrag(y: Int) {
        layoutParams.y = y - SizeUtil.getStatusBarHeight(context)
        updateView()
        sp.globalLyricViewY = layoutParams.y
    }

    private fun updateView() {
        globalLyricView?.let { view ->
            windowManager.updateViewLayout(view, layoutParams)
        }
    }

    override fun tryHide() {
        if (sp.isShowGlobalLyric) {
            globalLyricView?.visibility = View.GONE
        }
    }

    override fun tryShow() {
        if (sp.isShowGlobalLyric) {
            globalLyricView?.visibility = View.VISIBLE
        }
    }

    override fun onLogoClick() {
        val intent = Intent(context, MainActivity::class.java).apply {
            action = Constant.ACTION_MUSIC_PLAYER_PAGE
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    override fun onLockClick() {
        lock()
    }

    override fun onPreviousClick() {
        val manager = musicListManager
        manager.play(manager.previous())
    }

    override fun onPlayClick() {
        if (musicPlayerManager.isPlaying) {
            musicListManager.pause()
        } else {
            musicListManager.resume()
        }
    }

    override fun onNextClick() {
        val manager = musicListManager
        manager.play(manager.next())
    }

    companion object {
        @Volatile
        private var instance: GlobalLyricManagerImpl? = null

        @JvmStatic
        @Synchronized
        fun getInstance(context: Context): GlobalLyricManagerImpl {
            val current = instance
            if (current != null) {
                return current
            }

            return GlobalLyricManagerImpl(context).also {
                instance = it
            }
        }
    }
}
