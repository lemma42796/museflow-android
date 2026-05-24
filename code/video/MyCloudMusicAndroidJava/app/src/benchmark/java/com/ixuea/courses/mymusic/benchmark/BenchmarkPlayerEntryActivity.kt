package com.ixuea.courses.mymusic.benchmark

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.ixuea.courses.mymusic.component.player.activity.MusicPlayerActivity
import com.ixuea.courses.mymusic.component.song.model.Song
import com.ixuea.courses.mymusic.component.user.model.User
import com.ixuea.courses.mymusic.playback.PlaybackService

class BenchmarkPlayerEntryActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        seedPlayerQueue()
        startActivity(Intent(this, MusicPlayerActivity::class.java))
        finish()
    }

    private fun seedPlayerQueue() {
        val singer = User(BENCHMARK_SINGER_ID).apply {
            nickname = "MuseFlow Benchmark"
        }
        val song = Song().apply {
            id = BENCHMARK_SONG_ID
            title = "Benchmark Player Track"
            this.singer = singer
            singerId = singer.id
            singerNickname = singer.nickname
            duration = 234_000L
            progress = 0L
            isPlayList = true
        }

        PlaybackService.getListManager(applicationContext).datum = listOf(song)
    }

    private companion object {
        const val BENCHMARK_SONG_ID = "benchmark-player-track"
        const val BENCHMARK_SINGER_ID = "benchmark-player-singer"
    }
}
