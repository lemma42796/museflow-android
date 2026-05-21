package com.ixuea.courses.mymusic.manager

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.os.Looper

/**
 * 音频管理器，主要处理音频焦点获取、释放和音量调整。
 */
class SuperAudioManager(context: Context) {
    private val context: Context = context.applicationContext
    private val audioManager = this.context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var playbackAttributes: AudioAttributes? = null
    private var focusRequest: AudioFocusRequest? = null
    private val handler = Handler(Looper.myLooper() ?: Looper.getMainLooper())

    /**
     * 获取音频焦点。
     */
    fun requestAudioFocus(
        onAudioFocusChangeListener: AudioManager.OnAudioFocusChangeListener,
        contentType: Int,
    ): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            playbackAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(contentType)
                .build()

            focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(playbackAttributes!!)
                .setAcceptsDelayedFocusGain(true)
                .setWillPauseWhenDucked(true)
                .setOnAudioFocusChangeListener(onAudioFocusChangeListener, handler)
                .build()

            audioManager.requestAudioFocus(focusRequest!!)
        } else {
            @Suppress("DEPRECATION")
            audioManager.requestAudioFocus(
                onAudioFocusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )
        }
    }

    companion object {
        @Volatile
        private var instance: SuperAudioManager? = null

        @JvmStatic
        fun getInstance(context: Context): SuperAudioManager {
            return instance ?: synchronized(this) {
                instance ?: SuperAudioManager(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }
}
