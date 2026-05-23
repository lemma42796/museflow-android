package com.ixuea.courses.mymusic

import android.app.Application
import androidx.emoji.bundled.BundledEmojiCompatConfig
import androidx.emoji.text.EmojiCompat
import com.ixuea.android.downloader.DownloadService
import com.ixuea.android.downloader.callback.DownloadManager
import com.ixuea.courses.mymusic.component.chat.repository.ChatClient
import com.ixuea.courses.mymusic.component.login.model.Session
import com.ixuea.courses.mymusic.config.Config
import com.ixuea.courses.mymusic.manager.impl.MusicListManagerImpl
import com.ixuea.courses.mymusic.util.LiteORMUtil
import com.ixuea.courses.mymusic.util.PreferenceUtil
import com.tencent.mmkv.MMKV
import dagger.hilt.android.HiltAndroidApp
import io.rong.imlib.RongIMClient
import io.rong.imlib.listener.OnReceiveMessageWrapperListener
import io.rong.imlib.model.Message
import io.rong.imlib.model.ReceivedProfile
import timber.log.Timber

/**
 * Public slim Application.
 *
 * Frozen integrations such as payment, sharing, map startup, push vendors,
 * OCR, ads and update SDKs are intentionally not initialized on this branch.
 */
@HiltAndroidApp
class AppContext : Application() {
    lateinit var preference: PreferenceUtil
        private set

    lateinit var chatClient: RongIMClient
        private set

    val orm: LiteORMUtil
        get() = LiteORMUtil.getInstance(applicationContext)

    val downloadManager: DownloadManager
        get() {
            val existingManager = DownloadService.downloadManager
            if (existingManager != null) {
                return existingManager
            }

            val config = com.ixuea.android.downloader.config.Config().apply {
                databaseName = String.format("download_info_%s.db", preference.userId)
            }
            return DownloadService.getDownloadManager(applicationContext, config).also { manager ->
                DownloadService.downloadManager = manager
            }
        }

    override fun onCreate() {
        super.onCreate()
        instance = this
        MMKV.initialize(this)
        Timber.plant(Timber.DebugTree())
        preference = PreferenceUtil.getInstance(this)
        EmojiCompat.init(BundledEmojiCompatConfig(this))
        initIM()
    }

    fun onLogin(data: Session?) {
        val chatToken = data?.chatToken
        if (chatToken != null) {
            connectChat(chatToken)
        }
    }

    fun logout() {
        RongIMClient.getInstance().logout()
        MusicListManagerImpl.destroy()
        LiteORMUtil.destroy()

        val manager = DownloadService.downloadManager
        if (manager != null) {
            manager.destroy()
            DownloadService.downloadManager = null
        }
    }

    @Suppress("DEPRECATION")
    private fun initIM() {
        RongIMClient.init(applicationContext, Config.IM_KEY, true)
        chatClient = RongIMClient.getInstance()
        RongIMClient.addOnReceiveMessageListener(
            object : OnReceiveMessageWrapperListener() {
                override fun onReceivedMessage(message: Message, profile: ReceivedProfile) {
                    Timber.d("chat onReceived %s", message)
                    ChatClient.INSTANCE.onMessageReceived(message)
                }
            },
        )
    }

    private fun connectChat(chatToken: String) {
        RongIMClient.connect(
            chatToken,
            object : RongIMClient.ConnectCallback() {
                override fun onSuccess(userId: String?) {
                    Timber.d("connect chat success %s", userId)
                }

                override fun onError(errorCode: RongIMClient.ConnectionErrorCode?) {
                    Timber.e("connect chat error %s", errorCode)
                }

                override fun onDatabaseOpened(databaseOpenStatus: RongIMClient.DatabaseOpenStatus?) {
                }
            },
        )
    }

    companion object {
        private lateinit var instance: AppContext

        @JvmStatic
        fun getInstance(): AppContext {
            return instance
        }
    }
}
