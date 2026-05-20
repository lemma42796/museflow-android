package com.ixuea.courses.mymusic.playback

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.component.song.model.Song
import com.ixuea.courses.mymusic.manager.MusicPlayerListener
import com.ixuea.courses.mymusic.manager.MusicPlayerManager
import com.ixuea.courses.mymusic.util.Constant
import com.ixuea.courses.mymusic.util.ResourceUtil
import com.ixuea.courses.mymusic.util.WidgetUtil

/**
 * Keeps non-UI playback side effects alive after callers move away from the legacy service.
 */
internal class PlaybackUiBridge private constructor(context: Context) : MusicPlayerListener {
    private val appContext = context.applicationContext
    private var lastWidgetProgressTime: Long = 0

    override fun onPaused(data: Song) {
        WidgetUtil.onPaused(appContext)
    }

    override fun onPlaying(data: Song) {
        WidgetUtil.onPlaying(appContext)
    }

    override fun onProgress(data: Song) {
        val currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis - lastWidgetProgressTime > Constant.SAVE_PROGRESS_TIME) {
            WidgetUtil.onProgress(appContext, data)
            lastWidgetProgressTime = currentTimeMillis
        }
    }

    override fun onPrepared(mp: MediaPlayer?, data: Song) {
        Glide.with(appContext)
            .asBitmap()
            .load(ResourceUtil.resourceUri(data.icon))
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?,
                ) {
                    WidgetUtil.onPrepared(appContext, data, resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    val icon = BitmapFactory.decodeResource(
                        appContext.resources,
                        R.drawable.placeholder
                    )
                    WidgetUtil.onPrepared(appContext, data, icon)
                }
            })
    }

    companion object {
        @Volatile
        private var instance: PlaybackUiBridge? = null

        @JvmStatic
        fun ensureStarted(context: Context, musicPlayerManager: MusicPlayerManager) {
            val bridge = instance ?: synchronized(this) {
                instance ?: PlaybackUiBridge(context.applicationContext).also { instance = it }
            }
            musicPlayerManager.addMusicPlayerListener(bridge)
        }
    }
}
