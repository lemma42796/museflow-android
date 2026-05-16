package com.ixuea.courses.mymusic.playback

import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.ixuea.courses.mymusic.service.MusicPlayerService

class PlaybackService : MediaSessionService() {
    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()
        val repository = PlaybackRepository.getInstance(applicationContext)
        val musicListManager = MusicPlayerService.getListManager(applicationContext)
        val sessionPlayer = LegacyMusicSessionPlayer(repository.player, musicListManager)
        mediaSession = MediaSession.Builder(this, sessionPlayer)
            .setId("MyCloudMusicPlayback")
            .build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onDestroy() {
        mediaSession?.release()
        mediaSession = null
        super.onDestroy()
    }
}
