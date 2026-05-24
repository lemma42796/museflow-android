package com.ixuea.courses.mymusic.benchmark

import android.content.Context
import com.ixuea.courses.mymusic.component.song.model.Song
import com.ixuea.courses.mymusic.component.user.model.User
import com.ixuea.courses.mymusic.playback.PlaybackService
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder

internal object BenchmarkPlayerFixture {
    fun seedPlayerQueue(context: Context): Song {
        val appContext = context.applicationContext
        val listManager = PlaybackService.getListManager(appContext)
        val existing = listManager.datum.firstOrNull { song -> song.id == BENCHMARK_SONG_ID }
        if (existing != null) {
            return existing
        }

        val song = createBenchmarkSong(appContext)
        listManager.datum = listOf(song)
        return song
    }

    private fun createBenchmarkSong(context: Context): Song {
        val singer = User(BENCHMARK_SINGER_ID).apply {
            nickname = "MuseFlow Benchmark"
        }
        return Song().apply {
            id = BENCHMARK_SONG_ID
            title = "Benchmark Player Track"
            this.singer = singer
            singerId = singer.id
            singerNickname = singer.nickname
            duration = BENCHMARK_AUDIO_DURATION_MS
            progress = 0L
            isPlayList = true
            source = Song.SOURCE_LOCAL
            path = ensureBenchmarkAudioFile(context).absolutePath
            lyric = BENCHMARK_LYRIC
        }
    }

    private fun ensureBenchmarkAudioFile(context: Context): File {
        val file = File(context.cacheDir, BENCHMARK_AUDIO_FILE)
        if (!file.exists() || file.length() == 0L) {
            file.writeBytes(createSilentWav())
        }
        return file
    }

    private fun createSilentWav(): ByteArray {
        val dataSize = SAMPLE_RATE * AUDIO_SECONDS * BYTES_PER_SAMPLE
        val buffer = ByteBuffer.allocate(WAV_HEADER_BYTES + dataSize)
            .order(ByteOrder.LITTLE_ENDIAN)

        buffer.putAscii("RIFF")
        buffer.putInt(36 + dataSize)
        buffer.putAscii("WAVE")
        buffer.putAscii("fmt ")
        buffer.putInt(16)
        buffer.putShort(1)
        buffer.putShort(CHANNELS.toShort())
        buffer.putInt(SAMPLE_RATE)
        buffer.putInt(SAMPLE_RATE * CHANNELS * BYTES_PER_SAMPLE)
        buffer.putShort((CHANNELS * BYTES_PER_SAMPLE).toShort())
        buffer.putShort(BITS_PER_SAMPLE.toShort())
        buffer.putAscii("data")
        buffer.putInt(dataSize)
        repeat(dataSize) {
            buffer.put(0)
        }
        return buffer.array()
    }

    private fun ByteBuffer.putAscii(value: String) {
        put(value.toByteArray(Charsets.US_ASCII))
    }

    private const val BENCHMARK_SONG_ID = "benchmark-player-track"
    private const val BENCHMARK_SINGER_ID = "benchmark-player-singer"
    private const val BENCHMARK_AUDIO_FILE = "museflow-benchmark-silence.wav"
    private const val BENCHMARK_AUDIO_DURATION_MS = 5_000L
    private const val SAMPLE_RATE = 8_000
    private const val AUDIO_SECONDS = 5
    private const val CHANNELS = 1
    private const val BITS_PER_SAMPLE = 16
    private const val BYTES_PER_SAMPLE = BITS_PER_SAMPLE / 8
    private const val WAV_HEADER_BYTES = 44
    private const val BENCHMARK_LYRIC = "[00:00.00]Benchmark intro\n" +
        "[00:01.00]Benchmark verse\n" +
        "[00:03.00]Benchmark chorus\n"
}
