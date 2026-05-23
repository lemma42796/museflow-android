package com.ixuea.courses.mymusic.component.music.repository

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.BaseColumns
import android.provider.MediaStore
import com.ixuea.courses.mymusic.component.song.model.Song
import com.ixuea.courses.mymusic.util.Constant
import com.ixuea.courses.mymusic.util.LiteORMUtil
import com.ixuea.courses.mymusic.util.StorageUtil
import com.ixuea.superui.util.BitmapUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File

class LocalMusicScanRepository private constructor() {
    @SuppressLint("Range")
    suspend fun scan(
        context: Context,
        onProgress: suspend (String) -> Unit,
    ): List<Song> = withContext(Dispatchers.IO) {
        val appContext = context.applicationContext
        val songs = ArrayList<Song>()
        val contentResolver = appContext.contentResolver
        val orm = LiteORMUtil.getInstance(appContext)
        val coroutineContext = currentCoroutineContext()

        contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            arrayOf(
                BaseColumns._ID,
                MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.AudioColumns.ARTIST,
                MediaStore.Audio.AudioColumns.ALBUM,
                MediaStore.Audio.AudioColumns.ALBUM_ID,
                MediaStore.Audio.AudioColumns.DATA,
                MediaStore.Audio.AudioColumns.DISPLAY_NAME,
                MediaStore.Audio.AudioColumns.SIZE,
                MediaStore.Audio.AudioColumns.DURATION,
            ),
            Constant.MEDIA_AUDIO_SELECTION,
            arrayOf(
                Constant.MUSIC_FILTER_SIZE.toString(),
                Constant.MUSIC_FILTER_DURATION.toString(),
            ),
            MediaStore.Audio.Media.DEFAULT_SORT_ORDER,
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                coroutineContext.ensureActive()

                val id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID))
                val title = cursor.getString(
                    cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE),
                )
                val artist = cursor.getString(
                    cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST),
                )
                val album = cursor.getString(
                    cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM),
                )
                val albumId = cursor.getLong(
                    cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID),
                )
                val path = cursor.getString(
                    cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA),
                )
                val duration = cursor.getLong(
                    cursor.getColumnIndex(MediaStore.Audio.Media.DURATION),
                )

                Timber.d("scan local music %d,%s,%s,%s", id, title, artist, album)

                val song = Song().apply {
                    this.id = id.toString()
                    icon = getAndSaveAlbum(appContext, contentResolver, albumId)
                    this.title = title
                    singerNickname = artist
                    this.duration = duration
                    this.path = StorageUtil.getAudioContentUri(id)
                    source = Song.SOURCE_LOCAL
                }

                songs += song
                orm.saveSong(song)
                withContext(Dispatchers.Main.immediate) {
                    onProgress(path.orEmpty())
                }
                delay(SCAN_ITEM_DELAY_MS)
            }
        }

        songs
    }

    private fun getAndSaveAlbum(
        context: Context,
        contentResolver: ContentResolver,
        data: Long,
    ): String? {
        if (data <= 0) {
            return null
        }

        val uri = ContentUris.withAppendedId(URI_ARTWORK, data)
        return try {
            contentResolver.openInputStream(uri)?.use { input ->
                val bitmap = BitmapFactory.decodeStream(input) ?: return null
                val file = File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), data.toString())
                BitmapUtil.saveToFile(bitmap, file)
                file.absolutePath
            }
        } catch (error: Exception) {
            error.printStackTrace()
            null
        }
    }

    companion object {
        private const val SCAN_ITEM_DELAY_MS = 500L
        private val URI_ARTWORK: Uri = Uri.parse("content://media/external/audio/albumart")

        @Volatile
        private var instance: LocalMusicScanRepository? = null

        @JvmStatic
        fun getInstance(): LocalMusicScanRepository {
            return instance ?: synchronized(this) {
                instance ?: LocalMusicScanRepository().also {
                    instance = it
                }
            }
        }
    }
}
