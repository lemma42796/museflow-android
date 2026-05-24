package com.ixuea.courses.mymusic.benchmark

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BenchmarkDownloadActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_REFRESH -> BenchmarkDownloadFixture.advanceDownloadingProgress()
            ACTION_CLEANUP -> BenchmarkDownloadFixture.clearDownloadingTasks()
        }
    }

    companion object {
        const val ACTION_REFRESH = "com.ixuea.courses.mymusic.benchmark.DOWNLOAD_REFRESH"
        const val ACTION_CLEANUP = "com.ixuea.courses.mymusic.benchmark.DOWNLOAD_CLEANUP"
    }
}
