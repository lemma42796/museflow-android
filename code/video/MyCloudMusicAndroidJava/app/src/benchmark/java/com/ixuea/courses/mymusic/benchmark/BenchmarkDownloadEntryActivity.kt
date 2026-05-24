package com.ixuea.courses.mymusic.benchmark

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.ixuea.courses.mymusic.component.download.activity.DownloadActivity
import com.ixuea.courses.mymusic.component.download.ui.DOWNLOAD_INITIAL_TAB_EXTRA

class BenchmarkDownloadEntryActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BenchmarkDownloadFixture.seedDownloadingTasks(applicationContext)
        startActivity(
            Intent(this, DownloadActivity::class.java)
                .putExtra(DOWNLOAD_INITIAL_TAB_EXTRA, DOWNLOADING_TAB_INDEX),
        )
        finish()
    }

    private companion object {
        const val DOWNLOADING_TAB_INDEX = 1
    }
}
