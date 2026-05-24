package com.ixuea.courses.mymusic.benchmark

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ixuea.courses.mymusic.component.player.domain.NotifyRecordClickedUseCase
import com.ixuea.courses.mymusic.playback.PlaybackService

class BenchmarkPlaybackActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val appContext = context.applicationContext
        val song = BenchmarkPlayerFixture.seedPlayerQueue(appContext)
        val listManager = PlaybackService.getListManager(appContext)

        when (intent.getStringExtra(EXTRA_COMMAND)) {
            COMMAND_PLAY -> listManager.play(song)
            COMMAND_PAUSE -> listManager.pause()
            COMMAND_RESUME -> listManager.resume()
            COMMAND_SEEK -> listManager.seekTo(
                intent.getIntExtra(EXTRA_POSITION_MS, DEFAULT_SEEK_POSITION_MS)
            )
            COMMAND_SHOW_LYRIC -> NotifyRecordClickedUseCase().invoke()
        }
    }

    companion object {
        const val ACTION = "com.ixuea.courses.mymusic.benchmark.PLAYBACK_ACTION"
        const val EXTRA_COMMAND = "command"
        const val EXTRA_POSITION_MS = "position_ms"
        const val COMMAND_PLAY = "play"
        const val COMMAND_PAUSE = "pause"
        const val COMMAND_RESUME = "resume"
        const val COMMAND_SEEK = "seek"
        const val COMMAND_SHOW_LYRIC = "show_lyric"
        private const val DEFAULT_SEEK_POSITION_MS = 1_500
    }
}
