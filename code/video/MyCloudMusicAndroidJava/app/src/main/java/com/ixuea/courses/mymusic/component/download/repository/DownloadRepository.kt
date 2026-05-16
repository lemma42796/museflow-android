package com.ixuea.courses.mymusic.component.download.repository

import com.ixuea.android.downloader.callback.DownloadManager
import com.ixuea.android.downloader.domain.DownloadInfo
import com.ixuea.courses.mymusic.AppContext
import com.ixuea.courses.mymusic.component.song.model.Song
import com.ixuea.courses.mymusic.util.LiteORMUtil

/**
 * Narrow facade around the downloader SDK for the download screens.
 */
class DownloadRepository private constructor() {
    fun findDownloading(): List<DownloadInfo> {
        return manager().findAllDownloading()
    }

    fun findDownloaded(): List<DownloadInfo> {
        return manager().findAllDownloaded()
    }

    fun getDownloadById(id: String): DownloadInfo? {
        return manager().getDownloadById(id)
    }

    fun findDownloadedSongs(orm: LiteORMUtil): List<Song> {
        return findDownloaded().mapNotNull { downloadInfo ->
            orm.querySong(downloadInfo.id)
        }
    }

    fun resume(data: DownloadInfo) {
        manager().resume(data)
    }

    fun pause(data: DownloadInfo) {
        manager().pause(data)
    }

    fun remove(data: DownloadInfo) {
        manager().remove(data)
    }

    fun download(data: DownloadInfo) {
        manager().download(data)
    }

    fun resumeAll() {
        manager().resumeAll()
    }

    fun pauseAll() {
        manager().pauseAll()
    }

    fun isDownloading(data: List<DownloadInfo>): Boolean {
        return data.any { downloadInfo ->
            downloadInfo.status == DownloadInfo.STATUS_DOWNLOADING
        }
    }

    private fun manager(): DownloadManager {
        return AppContext.getInstance().downloadManager
    }

    companion object {
        @Volatile
        private var instance: DownloadRepository? = null

        @JvmStatic
        fun getInstance(): DownloadRepository {
            return instance ?: synchronized(this) {
                instance ?: DownloadRepository().also {
                    instance = it
                }
            }
        }
    }
}
