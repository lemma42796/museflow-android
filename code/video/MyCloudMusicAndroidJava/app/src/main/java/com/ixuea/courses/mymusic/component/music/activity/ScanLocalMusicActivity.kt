package com.ixuea.courses.mymusic.component.music.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.activity.BaseLogicActivity
import com.ixuea.courses.mymusic.component.music.domain.NotifyLocalMusicScanCompleteUseCase
import com.ixuea.courses.mymusic.component.music.domain.ScanLocalMusicUseCase
import com.ixuea.courses.mymusic.component.music.ui.ScanLocalMusicScreen
import com.ixuea.courses.mymusic.ui.compose.MuseFlowTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * 扫描本地音乐界面
 */
class ScanLocalMusicActivity : BaseLogicActivity() {
    private var isScanComplete by mutableStateOf(false)
    private var isScanning by mutableStateOf(false)
    private var progressText by mutableStateOf("")
    private var scanJob: Job? = null
    private var hasFoundMusic = false
    private val scanLocalMusic = ScanLocalMusicUseCase()
    private val notifyScanComplete = NotifyLocalMusicScanCompleteUseCase()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MuseFlowTheme {
                ScanLocalMusicScreen(
                    progressText = progressText,
                    isScanning = isScanning,
                    isScanComplete = isScanComplete,
                    onBack = { onBackPressedDispatcher.onBackPressed() },
                    onScanClick = ::onScanClick,
                )
            }
        }
    }

    fun onScanClick() {
        if (isScanComplete) {
            finish()
            return
        }

        if (isScanning) {
            stopScan()
        } else {
            startScan()
        }
    }

    private fun startScan() {
        isScanning = true
        startScanMusic()
    }

    private fun startScanMusic() {
        scanJob?.cancel()
        scanJob = lifecycleScope.launch {
            val songs = scanLocalMusic(applicationContext) { path ->
                progressText = path
            }
            scanJob = null
            isScanComplete = true
            isScanning = false
            hasFoundMusic = songs.isNotEmpty()
            progressText = resources.getString(R.string.found_music_count, songs.size)
        }
    }

    private fun stopScan() {
        scanJob?.cancel()
        scanJob = null
        isScanning = false
    }

    override fun onDestroy() {
        if (hasFoundMusic) {
            notifyScanComplete()
        }

        stopScan()
        super.onDestroy()
    }
}
