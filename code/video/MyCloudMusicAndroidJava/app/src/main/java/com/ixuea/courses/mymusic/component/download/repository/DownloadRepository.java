package com.ixuea.courses.mymusic.component.download.repository;

import com.ixuea.android.downloader.callback.DownloadManager;
import com.ixuea.android.downloader.domain.DownloadInfo;
import com.ixuea.courses.mymusic.AppContext;
import com.ixuea.courses.mymusic.component.song.model.Song;
import com.ixuea.courses.mymusic.util.LiteORMUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Narrow facade around the downloader SDK for the download screens.
 */
public class DownloadRepository {
    private static DownloadRepository instance;

    public synchronized static DownloadRepository getInstance() {
        if (instance == null) {
            instance = new DownloadRepository();
        }
        return instance;
    }

    public List<DownloadInfo> findDownloading() {
        return manager().findAllDownloading();
    }

    public List<DownloadInfo> findDownloaded() {
        return manager().findAllDownloaded();
    }

    public DownloadInfo getDownloadById(String id) {
        return manager().getDownloadById(id);
    }

    public List<Song> findDownloadedSongs(LiteORMUtil orm) {
        List<DownloadInfo> downloads = findDownloaded();
        ArrayList<Song> results = new ArrayList<>();
        for (DownloadInfo downloadInfo : downloads) {
            Song song = orm.querySong(downloadInfo.getId());
            if (song != null) {
                results.add(song);
            }
        }
        return results;
    }

    public void resume(DownloadInfo data) {
        manager().resume(data);
    }

    public void pause(DownloadInfo data) {
        manager().pause(data);
    }

    public void remove(DownloadInfo data) {
        manager().remove(data);
    }

    public void download(DownloadInfo data) {
        manager().download(data);
    }

    public void resumeAll() {
        manager().resumeAll();
    }

    public void pauseAll() {
        manager().pauseAll();
    }

    public boolean isDownloading(List<DownloadInfo> data) {
        for (DownloadInfo downloadInfo : data) {
            if (downloadInfo.getStatus() == DownloadInfo.STATUS_DOWNLOADING) {
                return true;
            }
        }
        return false;
    }

    private DownloadManager manager() {
        return AppContext.getInstance().getDownloadManager();
    }
}
