package com.ixuea.courses.mymusic.benchmark

import android.content.Context
import com.ixuea.android.downloader.DownloadService
import com.ixuea.android.downloader.callback.DownloadManager
import com.ixuea.android.downloader.db.DownloadDBController
import com.ixuea.android.downloader.domain.DownloadInfo
import com.ixuea.android.downloader.domain.DownloadThreadInfo
import java.io.File

internal object BenchmarkDownloadFixture {
    private const val BENCHMARK_DOWNLOAD_COUNT = 18
    private const val DOWNLOAD_SIZE_BYTES = 12L * 1024L * 1024L
    private const val DOWNLOAD_PROGRESS_STEP_BYTES = 512L * 1024L
    private const val DOWNLOAD_ROW_OFFSET_BYTES = 16L * 1024L

    private val BENCHMARK_DOWNLOAD_IDS = List(BENCHMARK_DOWNLOAD_COUNT) { index ->
        "benchmark-download-${index + 1}"
    }

    fun seedDownloadingTasks(context: Context) {
        val appContext = context.applicationContext
        val downloads = BENCHMARK_DOWNLOAD_IDS.mapIndexed { index, id ->
            createDownloadInfo(appContext, id, index)
        }
        DownloadService.downloadManager = BenchmarkDownloadManager(downloads)
    }

    fun advanceDownloadingProgress() {
        val manager = DownloadService.downloadManager as? BenchmarkDownloadManager ?: return
        manager.findAllDownloading()
            .filter { download -> download.id in BENCHMARK_DOWNLOAD_IDS }
            .forEachIndexed { index, download ->
                download.status = DownloadInfo.STATUS_DOWNLOADING
                download.size = DOWNLOAD_SIZE_BYTES
                download.progress = nextProgress(download.progress, index)
                download.downloadListener?.onDownloading(download.progress, download.size)
            }
    }

    fun clearDownloadingTasks() {
        val manager = DownloadService.downloadManager
        if (manager is BenchmarkDownloadManager) {
            manager.destroy()
            DownloadService.downloadManager = null
        }
    }

    private fun createDownloadInfo(context: Context, id: String, index: Int): DownloadInfo {
        val path = File(context.cacheDir, "$id.mp3").absolutePath
        return DownloadInfo.Builder()
            .setId(id)
            .setUrl("https://benchmark.museflow.local/$id.mp3")
            .setPath(path)
            .setCreateAt(System.currentTimeMillis() - index)
            .build()
            .apply {
                status = DownloadInfo.STATUS_DOWNLOADING
                size = DOWNLOAD_SIZE_BYTES
                progress = (DOWNLOAD_PROGRESS_STEP_BYTES * (index + 1)).coerceAtMost(size - 1)
            }
    }

    private fun nextProgress(progress: Long, index: Int): Long {
        val next = progress + DOWNLOAD_PROGRESS_STEP_BYTES + index * DOWNLOAD_ROW_OFFSET_BYTES
        return if (next >= DOWNLOAD_SIZE_BYTES) {
            DOWNLOAD_PROGRESS_STEP_BYTES
        } else {
            next
        }
    }

}

private class BenchmarkDownloadManager(
    downloads: List<DownloadInfo>,
) : DownloadManager {
    private val downloading = downloads.toMutableList()
    private val downloaded = mutableListOf<DownloadInfo>()
    private val noOpDbController = NoOpDownloadDBController()

    override fun download(downloadInfo: DownloadInfo) {
        if (downloading.none { it.id == downloadInfo.id }) {
            downloading.add(downloadInfo)
        }
        resume(downloadInfo)
    }

    override fun pause(downloadInfo: DownloadInfo) {
        downloadInfo.status = DownloadInfo.STATUS_PAUSED
        downloadInfo.downloadListener?.onPaused()
    }

    override fun resume(downloadInfo: DownloadInfo) {
        downloadInfo.status = DownloadInfo.STATUS_DOWNLOADING
        downloadInfo.downloadListener?.onStart()
    }

    override fun remove(downloadInfo: DownloadInfo) {
        downloadInfo.status = DownloadInfo.STATUS_REMOVED
        downloading.removeAll { it.id == downloadInfo.id }
        downloaded.removeAll { it.id == downloadInfo.id }
        downloadInfo.downloadListener?.onRemoved()
    }

    override fun destroy() {
        downloading.clear()
        downloaded.clear()
    }

    override fun getDownloadById(id: String): DownloadInfo? {
        return downloading.firstOrNull { it.id == id }
            ?: downloaded.firstOrNull { it.id == id }
    }

    override fun findAllDownloading(): MutableList<DownloadInfo> {
        return downloading
    }

    override fun findAllDownloaded(): MutableList<DownloadInfo> {
        return downloaded
    }

    override fun getDownloadDBController(): DownloadDBController {
        return noOpDbController
    }

    override fun resumeAll() {
        downloading.forEach(::resume)
    }

    override fun pauseAll() {
        downloading.forEach(::pause)
    }

    override fun onDownloadFailed(downloadInfo: DownloadInfo) {
        downloadInfo.status = DownloadInfo.STATUS_ERROR
        downloadInfo.downloadListener?.onDownloadFailed(null)
    }
}

private class NoOpDownloadDBController : DownloadDBController {
    override fun findAllDownloading(): MutableList<DownloadInfo> = mutableListOf()

    override fun findAllDownloaded(): MutableList<DownloadInfo> = mutableListOf()

    override fun findDownloadedInfoById(id: String?): DownloadInfo? = null

    override fun pauseAllDownloading() = Unit

    override fun createOrUpdate(downloadInfo: DownloadInfo?) = Unit

    override fun createOrUpdate(downloadThreadInfo: DownloadThreadInfo?) = Unit

    override fun delete(downloadInfo: DownloadInfo?) = Unit

    override fun delete(download: DownloadThreadInfo?) = Unit
}
