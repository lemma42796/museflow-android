package com.ixuea.courses.mymusic.component.download.listener

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import com.ixuea.android.downloader.callback.AbsDownloadListener
import com.ixuea.android.downloader.exception.DownloadException
import java.lang.ref.SoftReference

/**
 * 下载监听器
 * 将所有回调都调用onRefresh
 */
abstract class MyDownloadListener : AbsDownloadListener {
    private val mainHandler = Handler(Looper.getMainLooper())
    private var lastProgressRefreshTime = 0L

    constructor()

    constructor(userTag: SoftReference<Any>) : super(userTag)

    override fun onStart() {
        postRefresh()
    }

    override fun onWaited() {
        postRefresh()
    }

    override fun onPaused() {
        postRefresh()
    }

    override fun onDownloading(progress: Long, size: Long) {
        val now = SystemClock.uptimeMillis()
        if (now - lastProgressRefreshTime >= PROGRESS_THROTTLE_MS || (size > 0 && progress >= size)) {
            lastProgressRefreshTime = now
            postRefresh()
        }
    }

    override fun onRemoved() {
        postRefresh()
    }

    override fun onDownloadSuccess() {
        postRefresh()
    }

    override fun onDownloadFailed(e: DownloadException?) {
        postRefresh()
    }

    abstract fun onRefresh()

    private fun postRefresh() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            onRefresh()
        } else {
            mainHandler.post { onRefresh() }
        }
    }

    companion object {
        private const val PROGRESS_THROTTLE_MS = 300L
    }
}
