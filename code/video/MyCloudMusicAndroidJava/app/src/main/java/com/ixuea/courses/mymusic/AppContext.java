package com.ixuea.courses.mymusic;

import static com.ixuea.android.downloader.DownloadService.downloadManager;

import android.app.Application;

import com.ixuea.android.downloader.DownloadService;
import com.ixuea.android.downloader.callback.DownloadManager;
import com.ixuea.courses.mymusic.component.chat.repository.ChatClient;
import com.ixuea.courses.mymusic.component.login.model.Session;
import com.ixuea.courses.mymusic.config.Config;
import com.ixuea.courses.mymusic.manager.impl.MusicListManagerImpl;
import com.ixuea.courses.mymusic.util.LiteORMUtil;
import com.ixuea.courses.mymusic.util.PreferenceUtil;
import com.tencent.mmkv.MMKV;

import dagger.hilt.android.HiltAndroidApp;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.listener.OnReceiveMessageWrapperListener;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.ReceivedProfile;
import timber.log.Timber;

/**
 * Public slim Application.
 *
 * Frozen integrations such as payment, sharing, map startup, push vendors,
 * OCR, ads and update SDKs are intentionally not initialized on this branch.
 */
@HiltAndroidApp
public class AppContext extends Application {
    private static AppContext instance;
    private PreferenceUtil sp;
    private RongIMClient chatClient;

    public static AppContext getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        MMKV.initialize(this);
        Timber.plant(new Timber.DebugTree());
        sp = PreferenceUtil.getInstance(this);
        initIM();
    }

    public RongIMClient getChatClient() {
        return chatClient;
    }

    public PreferenceUtil getPreference() {
        return sp;
    }

    public LiteORMUtil getOrm() {
        return LiteORMUtil.getInstance(getApplicationContext());
    }

    public void onLogin(Session data) {
        if (data != null && data.getChatToken() != null) {
            connectChat(data);
        }
    }

    public void logout() {
        RongIMClient.getInstance().logout();
        MusicListManagerImpl.destroy();
        LiteORMUtil.destroy();
        if (downloadManager != null) {
            downloadManager.destroy();
            downloadManager = null;
        }
    }

    public DownloadManager getDownloadManager() {
        if (downloadManager == null) {
            com.ixuea.android.downloader.config.Config config =
                    new com.ixuea.android.downloader.config.Config();
            config.setDatabaseName(String.format("download_info_%s.db", sp.getUserId()));
            downloadManager = DownloadService.getDownloadManager(getApplicationContext(), config);
        }
        return downloadManager;
    }

    private void initIM() {
        RongIMClient.init(getApplicationContext(), Config.IM_KEY, true);
        chatClient = RongIMClient.getInstance();
        chatClient.addOnReceiveMessageListener(new OnReceiveMessageWrapperListener() {
            @Override
            public void onReceivedMessage(Message message, ReceivedProfile profile) {
                Timber.d("chat onReceived %s", message);
                ChatClient.INSTANCE.onMessageReceived(message);
            }
        });
    }

    private void connectChat(Session data) {
        RongIMClient.connect(data.getChatToken(), new RongIMClient.ConnectCallback() {
            @Override
            public void onSuccess(String userId) {
                Timber.d("connect chat success %s", userId);
            }

            @Override
            public void onError(RongIMClient.ConnectionErrorCode errorCode) {
                Timber.e("connect chat error %s", errorCode);
            }

            @Override
            public void onDatabaseOpened(RongIMClient.DatabaseOpenStatus databaseOpenStatus) {
            }
        });
    }
}
