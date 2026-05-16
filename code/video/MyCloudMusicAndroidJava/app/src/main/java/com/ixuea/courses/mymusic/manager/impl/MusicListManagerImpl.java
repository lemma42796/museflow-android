package com.ixuea.courses.mymusic.manager.impl;

import android.content.Context;
import android.media.MediaPlayer;

import com.ixuea.android.downloader.domain.DownloadInfo;
import com.ixuea.courses.mymusic.AppContext;
import com.ixuea.courses.mymusic.component.song.model.Song;
import com.ixuea.courses.mymusic.manager.MusicListManager;
import com.ixuea.courses.mymusic.manager.MusicPlayerListener;
import com.ixuea.courses.mymusic.manager.MusicPlayerManager;
import com.ixuea.courses.mymusic.manager.model.event.MusicPlayListChangedEvent;
import com.ixuea.courses.mymusic.playback.PlaybackRepository;
import com.ixuea.courses.mymusic.service.MusicPlayerService;
import com.ixuea.courses.mymusic.util.Constant;
import com.ixuea.courses.mymusic.util.DataUtil;
import com.ixuea.courses.mymusic.util.LiteORMUtil;
import com.ixuea.courses.mymusic.util.PreferenceUtil;
import com.ixuea.courses.mymusic.util.ResourceUtil;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import timber.log.Timber;

/**
 * 列表管理器默认实现
 */
public class MusicListManagerImpl implements MusicListManager, MusicPlayerListener {
    private static MusicListManagerImpl instance;
    private final Context context;
    private final MusicPlayerManager musicPlayerManager;
    private final PlaybackRepository playbackRepository;
    /**
     * 列表
     */
    private List<Song> datum = new LinkedList<>();
    private boolean isPlay;
    private Song data;
    /**
     * 循环模式
     * 默认列表循环
     */
    private int model = MODEL_LOOP_LIST;
    private final PreferenceUtil sp;
    private long lastTime;

    private MusicListManagerImpl(Context context) {
        this.context = context.getApplicationContext();

        //初始化音乐播放管理器
        musicPlayerManager = MusicPlayerService.getMusicPlayerManager(this.context);
        playbackRepository = PlaybackRepository.getInstance(this.context);

        musicPlayerManager.addMusicPlayerListener(this);

        sp = PreferenceUtil.getInstance(this.context);

        //初始化播放列表
        initPlayList();

        updatePlaybackQueueState();
    }

    /**
     * 销毁实例
     */
    public static void destroy() {
        instance = null;
    }

    /**
     * 初始化播放列表
     */
    private void initPlayList() {
        //查询播放列表
        List<Song> datum = getOrm().queryPlayList();

        if (datum.size() > 0) {
            //添加到现在的播放列表
            this.datum.clear();
            this.datum.addAll(datum);

            //获取最后播放音乐id
            String id = sp.getLastPlaySongId();
            if (StringUtils.isNotBlank(id)) {
                //有最后播放音乐的id

                //在播放列表中找到该音乐
                for (Song s : datum) {
                    if (s.getId().equals(id)) {
                        //找到了
                        data = s;
                        break;
                    }
                }

                if (data == null) {
                    //表示没找到
                    //可能各种原因
                    defaultPlaySong();
                } else {
                    //找到了
                }
            } else {
                //默认就是第一首
                defaultPlaySong();
            }
        }
    }

    /**
     * 设置默认播放音乐
     */
    private void defaultPlaySong() {
        data = datum.get(0);
    }

    public synchronized static MusicListManager getInstance(Context context) {
        if (instance == null) {
            instance = new MusicListManagerImpl(context);
        }
        return instance;
    }

    @Override
    public List<Song> getDatum() {
        return datum;
    }

    @Override
    public void setDatum(List<Song> datum) {
        //将原来数据playList标志设置为false
        DataUtil.changePlayListFlag(this.datum, false);

        //保存到数据库
        saveAll();

        //清空原来的数据
        this.datum.clear();

        //添加新的数据
        this.datum.addAll(datum);

        //更改播放列表标志
        DataUtil.changePlayListFlag(this.datum, true);

        //保存到数据库
        saveAll();

        updatePlaybackQueueState();
        sendPlayListChangedEvent(0);
    }

    /**
     * 保存播放列表
     */
    private void saveAll() {
        getOrm().saveAll(datum);
    }

    private LiteORMUtil getOrm() {
        return LiteORMUtil.getInstance(this.context);
    }

    @Override
    public void play(Song data) {
        if (data == null) {
            Timber.w("Ignore play request because song is null");
            return;
        }

        //当前音乐黑胶唱片滚动
        data.setRotate(true);

        //标记已经播放了
        isPlay = true;

        //保存数据
        this.data = data;
        updatePlaybackQueueState();

        if (StringUtils.isNotBlank(data.getPath())) {
            //本地音乐
            //不拼接地址
            musicPlayerManager.play(data.getPath(), data);
        } else {
            //判断是否有下载对象
            DownloadInfo downloadInfo = AppContext.getInstance().getDownloadManager().getDownloadById(data.getId());
            if (downloadInfo != null && downloadInfo.getStatus() == DownloadInfo.STATUS_COMPLETED) {
                //下载完成了

                //播放本地音乐
                musicPlayerManager.play(downloadInfo.getPath(), data);
                Timber.d("play offline %s %s %s", data.getTitle(), downloadInfo.getPath(), data.getUri());
            } else {
                //播放在线音乐
                String path = ResourceUtil.resourceUri(data.getUri());

                musicPlayerManager.play(path, data);

                Timber.d("play online %s %s", data.getTitle(), path);
            }
        }

        //设置最后播放音乐的Id
        sp.setLastPlaySongId(data.getId());
    }

    @Override
    public void pause() {
        musicPlayerManager.pause();
    }

    @Override
    public void resume() {
        if (data == null) {
            Timber.w("Ignore resume request because current song is null");
            return;
        }

        if (isPlay) {
            //原来已经播放过
            //也就说播放器已经初始化了
            musicPlayerManager.resume();
        } else {
            //到这里，是应用开启后，第一次点继续播放
            //而这时内部其实还没有准备播放，所以应该调用播放
            play(data);

            //判断是否需要继续播放
            if (data.getProgress() > 0) {
                //有播放进度

                //就从上一次位置开始播放
                musicPlayerManager.seekTo((int) data.getProgress());
            }
        }
    }

    @Override
    public Song getData() {
        return data;
    }

    @Override
    public int changeLoopModel() {
        //循环模式+1
        model++;

        //判断循环模式边界
        if (model > MODEL_LOOP_RANDOM) {
            //如果当前循环模式
            //大于最后一个循环模式
            //就设置为第0个循环模式
            model = MODEL_LOOP_LIST;
        }

        //判断是否是单曲循环
        if (MODEL_LOOP_ONE == model) {
            //单曲循环模式
            //设置到mediaPlay
            musicPlayerManager.setLooping(true);
        } else {
            //其他模式关闭循环
            musicPlayerManager.setLooping(false);
        }

        //返回最终的循环模式
        updatePlaybackQueueState();
        return model;
    }

    @Override
    public int getLoopModel() {
        return model;
    }

    @Override
    public Song previous() {
        if (datum.size() == 0) {
            //如果没有音乐了
            //直接返回null
            return null;
        }

        //音乐索引
        int index = 0;

        //判断循环模式
        switch (model) {
            case MODEL_LOOP_RANDOM:
                //随机循环

                //在0~datum.size()中
                //不包含datum.size()
                index = new Random().nextInt(datum.size());
                break;
            default:
                //找到当前音乐索引
                index = datum.indexOf(data);

                if (index != -1) {
                    //找到了

                    //如果当前播放是列表第一个
                    if (index == 0) {
                        //第一首音乐

                        //那就从最后开始播放
                        index = datum.size() - 1;
                    } else {
                        index--;
                    }
                } else {
                    //抛出异常
                    //因为正常情况下是能找到的
                    throw new IllegalArgumentException("Cant't find current song");
                }
                break;
        }

        //获取音乐
        return datum.get(index);
    }

    @Override
    public Song next() {
        if (datum.size() == 0) {
            //如果没有音乐了
            //直接返回null
            return null;
        }

        //音乐索引
        int index = 0;

        //判断循环模式
        switch (model) {
            case MODEL_LOOP_RANDOM:
                //随机循环

                //在0~datum.size()中
                //不包含datum.size()
                index = new Random().nextInt(datum.size());
                break;
            default:
                //找到当前音乐索引
                index = datum.indexOf(data);

                if (index != -1) {
                    //找到了

                    //如果当前播放是列表最后一个
                    if (index == datum.size() - 1) {
                        //最后一首音乐

                        //那就从0开始播放
                        index = 0;
                    } else {
                        index++;
                    }
                } else {
                    //抛出异常
                    //因为正常情况下是能找到的
                    throw new IllegalArgumentException("Cant'found current song");
                }
                break;
        }

        return datum.get(index);
    }

    @Override
    public void delete(int position) {
        //获取要删除的音乐
        Song song = datum.get(position);

        if (song.getId().equals(data.getId())) {
            //删除的音乐就是当前播放的音乐

            //应该停止当前播放
            pause();

            //并播放下一首音乐
            Song next = next();

            if (next.getId().equals(data.getId())) {
                //找到了自己
                //没有歌曲可以播放了
                data = null;
                //TODO Bug 随机循环的情况下有可能获取到自己
            } else {
                play(next);
            }
        }

        //直接删除
        datum.remove(song);

        //从数据库中删除
        getOrm().deleteSong(song);

        updatePlaybackQueueState();
        sendPlayListChangedEvent(position);
    }

    private void sendPlayListChangedEvent(int position) {
        EventBus.getDefault().post(new MusicPlayListChangedEvent(position));
    }

    /**
     * 播放完毕了回调
     *
     * @param mp
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        if (model == MODEL_LOOP_ONE) {
            //如果是单曲循环
            //就不会处理了
            //因为我们使用了MediaPlayer的循环模式

            //如果使用的第三方框架
            //如果没有循环模式
            //那就要在这里继续播放当前音乐
        } else {
            Song data = next();
            if (data != null) {
                play(data);
            }
        }
    }

    @Override
    public void onProgress(Song data) {
        //保存当前音乐播放进度
        //因为Android应用不太好监听应用被杀掉
        //所以这里就在这里保存
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - lastTime > Constant.SAVE_PROGRESS_TIME) {
            //保存数据
            getOrm().saveSong(data);

            lastTime = currentTimeMillis;
        }
    }

    @Override
    public void deleteAll() {
        //如果在播放音乐就暂停
        if (musicPlayerManager.isPlaying()) {
            pause();
        }

        //清空列表
        datum.clear();

        //从数据库中删除
        getOrm().deletePlayListSong();

        updatePlaybackQueueState();
        sendPlayListChangedEvent(-1);
    }

    @Override
    public void seekTo(int progress) {
        if (data == null) {
            Timber.w("Ignore seek request because current song is null");
            return;
        }

        if (!musicPlayerManager.isPlaying()) {
            resume();
        }

        musicPlayerManager.seekTo(progress);
    }

    private void updatePlaybackQueueState() {
        int currentIndex = data == null ? -1 : datum.indexOf(data);
        playbackRepository.setQueue(datum, currentIndex, model);
    }
}
