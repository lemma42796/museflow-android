package com.ixuea.courses.mymusic.component.download.listener;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import com.ixuea.android.downloader.callback.AbsDownloadListener;
import com.ixuea.android.downloader.exception.DownloadException;

import java.lang.ref.SoftReference;

/**
 * 下载监听器
 * 将所有回调都调用onRefresh
 */
public abstract class MyDownloadListener extends AbsDownloadListener {
    private static final long PROGRESS_THROTTLE_MS = 300;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private long lastProgressRefreshTime;

    public MyDownloadListener() {
    }

    public MyDownloadListener(SoftReference<Object> userTag) {
        super(userTag);
    }

    @Override
    public void onStart() {
        postRefresh();
    }

    @Override
    public void onWaited() {
        postRefresh();
    }

    @Override
    public void onPaused() {
        postRefresh();
    }

    @Override
    public void onDownloading(long progress, long size) {
        long now = SystemClock.uptimeMillis();
        if (now - lastProgressRefreshTime >= PROGRESS_THROTTLE_MS || (size > 0 && progress >= size)) {
            lastProgressRefreshTime = now;
            postRefresh();
        }
    }

    @Override
    public void onRemoved() {
        postRefresh();
    }

    @Override
    public void onDownloadSuccess() {
        postRefresh();
    }

    @Override
    public void onDownloadFailed(DownloadException e) {
        postRefresh();
    }

    public abstract void onRefresh();

    private void postRefresh() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            onRefresh();
        } else {
            mainHandler.post(this::onRefresh);
        }
    }
}
