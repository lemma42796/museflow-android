package com.ixuea.courses.mymusic.component.widget

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.glance.appwidget.updateAll
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.component.song.model.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

internal data class MusicWidgetSnapshot(
    val title: String,
    val progress: Int,
    val duration: Int,
    val isPlaying: Boolean,
    val isGlobalLyricShown: Boolean,
) {
    val progressFraction: Float
        get() = if (duration <= 0) {
            0f
        } else {
            (progress.toFloat() / duration.toFloat()).coerceIn(0f, 1f)
        }
}

internal object MusicWidgetStore {
    private const val PREFS = "music_widget_state"
    private const val KEY_TITLE = "title"
    private const val KEY_PROGRESS = "progress"
    private const val KEY_DURATION = "duration"
    private const val KEY_PLAYING = "playing"
    private const val KEY_GLOBAL_LYRIC = "global_lyric"
    private const val COVER_FILE = "music_widget_cover.png"
    private const val COVER_SIZE = 192

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    fun readSnapshot(context: Context): MusicWidgetSnapshot {
        val prefs = prefs(context)
        val title = prefs.getString(KEY_TITLE, context.getString(R.string.empty_song_tip))
            ?: context.getString(R.string.empty_song_tip)
        return MusicWidgetSnapshot(
            title = title,
            progress = prefs.getInt(KEY_PROGRESS, 0),
            duration = prefs.getInt(KEY_DURATION, 100),
            isPlaying = prefs.getBoolean(KEY_PLAYING, false),
            isGlobalLyricShown = prefs.getBoolean(KEY_GLOBAL_LYRIC, false),
        )
    }

    fun readCover(context: Context): Bitmap? {
        val file = coverFile(context)
        return if (file.exists()) {
            BitmapFactory.decodeFile(file.absolutePath)
        } else {
            null
        }
    }

    fun onPlaying(context: Context) {
        update(context) {
            putBoolean(KEY_PLAYING, true)
        }
    }

    fun onPaused(context: Context) {
        update(context) {
            putBoolean(KEY_PLAYING, false)
        }
    }

    fun onPrepared(context: Context, data: Song, icon: Bitmap) {
        val appContext = context.applicationContext
        scope.launch {
            withContext(Dispatchers.IO) {
                saveCover(appContext, icon)
                prefs(appContext).edit()
                    .putString(KEY_TITLE, formatTitle(data))
                    .putInt(KEY_PROGRESS, data.progress.toInt())
                    .putInt(KEY_DURATION, data.duration.toInt())
                    .commit()
            }
            MusicGlanceWidget.updateAll(appContext)
        }
    }

    fun onProgress(context: Context, data: Song) {
        update(context) {
            putString(KEY_TITLE, formatTitle(data))
            putInt(KEY_PROGRESS, data.progress.toInt())
            putInt(KEY_DURATION, data.duration.toInt())
        }
    }

    fun onGlobalLyricShowStatusChanged(context: Context, isShow: Boolean) {
        update(context) {
            putBoolean(KEY_GLOBAL_LYRIC, isShow)
        }
    }

    private fun update(
        context: Context,
        block: SharedPreferences.Editor.() -> Unit,
    ) {
        val appContext = context.applicationContext
        scope.launch {
            withContext(Dispatchers.IO) {
                prefs(appContext).edit()
                    .apply(block)
                    .commit()
            }
            MusicGlanceWidget.updateAll(appContext)
        }
    }

    private fun saveCover(context: Context, icon: Bitmap) {
        val cover = scaleCover(icon)
        coverFile(context).outputStream().use { output ->
            cover.compress(Bitmap.CompressFormat.PNG, 100, output)
        }
    }

    private fun scaleCover(icon: Bitmap): Bitmap {
        return if (icon.width <= COVER_SIZE && icon.height <= COVER_SIZE) {
            icon
        } else {
            Bitmap.createScaledBitmap(icon, COVER_SIZE, COVER_SIZE, true)
        }
    }

    private fun formatTitle(data: Song): String {
        val title = data.title.orEmpty()
        val singer = data.singer?.nickname.orEmpty()
        return if (singer.isBlank()) {
            title
        } else {
            "$title - $singer"
        }
    }

    private fun prefs(context: Context): SharedPreferences {
        return context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    }

    private fun coverFile(context: Context): File {
        return File(context.applicationContext.filesDir, COVER_FILE)
    }
}
