package com.ixuea.courses.mymusic.component.download.listener;

import com.ixuea.android.downloader.callback.AbsDownloadListener;
import com.ixuea.android.downloader.exception.DownloadException;

import java.lang.ref.SoftReference;

/**
 * 下载监听器
 * 将所有回调都调用onRefresh
 */
public abstract class MyDownloadListener extends AbsDownloadListener {
    public MyDownloadListener() {
    }

    public MyDownloadListener(SoftReference<Object> userTag) {
        super(userTag);
    }

    @Override
    public void onStart() {
        onRefresh();
    }

    @Override
    public void onWaited() {
        onRefresh();
    }

    @Override
    public void onPaused() {
        onRefresh();
    }

    @Override
    public void onDownloading(long progress, long size) {
        onRefresh();
    }

    @Override
    public void onRemoved() {
        onRefresh();
    }

    @Override
    public void onDownloadSuccess() {
        onRefresh();
    }

    @Override
    public void onDownloadFailed(DownloadException e) {
        onRefresh();
    }

    public abstract void onRefresh();
}
