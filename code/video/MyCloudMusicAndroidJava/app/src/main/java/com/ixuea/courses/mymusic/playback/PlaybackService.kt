package com.ixuea.courses.mymusic.playback

import android.app.Notification
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.CommandButton
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionCommands
import androidx.media3.session.SessionResult
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.ixuea.courses.mymusic.R
import com.ixuea.courses.mymusic.manager.MusicListManager
import com.ixuea.courses.mymusic.manager.MusicPlayerManager
import com.ixuea.courses.mymusic.manager.impl.GlobalLyricManagerImpl
import com.ixuea.courses.mymusic.manager.impl.MusicListManagerImpl
import com.ixuea.courses.mymusic.manager.impl.MusicPlayerManagerImpl
import com.ixuea.courses.mymusic.util.Constant
import com.ixuea.courses.mymusic.util.IntentUtil
import com.ixuea.courses.mymusic.util.NotificationUtil
import com.ixuea.courses.mymusic.util.ServiceUtil

@OptIn(UnstableApi::class)
class PlaybackService : MediaSessionService() {
    private var mediaSession: MediaSession? = null
    private lateinit var musicListManager: MusicListManager
    private lateinit var musicPlayerManager: MusicPlayerManager
    private val lyricCommand = SessionCommand(Constant.ACTION_LYRIC, Bundle.EMPTY)
    private val sessionCallback = object : MediaSession.Callback {
        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
        ): MediaSession.ConnectionResult {
            return MediaSession.ConnectionResult.AcceptedResultBuilder(session)
                .setAvailableSessionCommands(createAvailableSessionCommands())
                .setCustomLayout(listOf(createLyricCommandButton()))
                .build()
        }

        override fun onCustomCommand(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
            customCommand: SessionCommand,
            args: Bundle,
        ): ListenableFuture<SessionResult> {
            return if (customCommand.customAction == Constant.ACTION_LYRIC) {
                toggleGlobalLyric()
                Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
            } else {
                Futures.immediateFuture(SessionResult(SessionResult.RESULT_ERROR_NOT_SUPPORTED))
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        setMediaNotificationProvider(createMediaNotificationProvider())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notification: Notification = NotificationUtil.getServiceForeground(applicationContext)
            startForeground(NOTIFICATION_ID, notification)
        }

        val repository = PlaybackRepository.getInstance(applicationContext)
        musicListManager = getListManager(applicationContext)
        musicPlayerManager = getMusicPlayerManager(applicationContext)
        val sessionPlayer = LegacyMusicSessionPlayer(repository.player, musicListManager)
        mediaSession = MediaSession.Builder(this, sessionPlayer)
            .setId("MyCloudMusicPlayback")
            .setSessionActivity(
                IntentUtil.createMainActivityPendingIntent(
                    applicationContext,
                    Constant.ACTION_MUSIC_PLAYER_PAGE
                )
            )
            .setCustomLayout(listOf(createLyricCommandButton()))
            .setCallback(sessionCallback)
            .build()
            .also { addSession(it) }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Constant.ACTION_PREVIOUS -> musicListManager.play(musicListManager.previous())
            Constant.ACTION_PLAY -> {
                if (musicPlayerManager.isPlaying) {
                    musicListManager.pause()
                } else {
                    musicListManager.resume()
                }
            }

            Constant.ACTION_NEXT -> musicListManager.play(musicListManager.next())
            Constant.ACTION_LYRIC -> toggleGlobalLyric()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onDestroy() {
        mediaSession?.release()
        mediaSession = null
        super.onDestroy()
    }

    private fun createMediaNotificationProvider(): DefaultMediaNotificationProvider {
        return DefaultMediaNotificationProvider.Builder(applicationContext)
            .setNotificationId(NOTIFICATION_ID)
            .setChannelId(NotificationUtil.CHANNEL_ID_MUSIC)
            .setChannelName(R.string.channel_music)
            .build()
            .apply {
                setSmallIcon(R.mipmap.ic_launcher)
            }
    }

    private fun createAvailableSessionCommands(): SessionCommands {
        return MediaSession.ConnectionResult.DEFAULT_SESSION_COMMANDS
            .buildUpon()
            .add(lyricCommand)
            .build()
    }

    private fun createLyricCommandButton(): CommandButton {
        return CommandButton.Builder()
            .setSessionCommand(lyricCommand)
            .setIconResId(R.drawable.ic_lyric)
            .setDisplayName("lyric")
            .setEnabled(true)
            .build()
    }

    private fun toggleGlobalLyric() {
        val globalLyricManager = GlobalLyricManagerImpl.getInstance(applicationContext)
        if (globalLyricManager.isShowing()) {
            globalLyricManager.hide()
        } else {
            globalLyricManager.show()
        }
    }

    companion object {
        private const val NOTIFICATION_ID = 100

        @JvmStatic
        fun start(context: Context) {
            ServiceUtil.startService(context.applicationContext, PlaybackService::class.java)
        }

        /**
         * 获取音乐播放Manager。
         */
        @JvmStatic
        fun getMusicPlayerManager(context: Context): MusicPlayerManager {
            val appContext = context.applicationContext
            val musicPlayerManager = MusicPlayerManagerImpl.getInstance(appContext)
            PlaybackUiBridge.ensureStarted(appContext, musicPlayerManager)
            return musicPlayerManager
        }

        /**
         * 获取列表管理器。
         */
        @JvmStatic
        fun getListManager(context: Context): MusicListManager {
            return MusicListManagerImpl.getInstance(context.applicationContext)
        }
    }
}
