package com.ixuea.courses.mymusic.manager.impl;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;

import com.ixuea.courses.mymusic.component.api.HttpObserver;
import com.ixuea.courses.mymusic.component.lyric.parser.LyricParser;
import com.ixuea.courses.mymusic.component.song.model.Song;
import com.ixuea.courses.mymusic.manager.MusicPlayerListener;
import com.ixuea.courses.mymusic.manager.MusicPlayerManager;
import com.ixuea.courses.mymusic.manager.SuperAudioManager;
import com.ixuea.courses.mymusic.model.response.DetailResponse;
import com.ixuea.courses.mymusic.playback.PlaybackController;
import com.ixuea.courses.mymusic.playback.PlaybackRepository;
import com.ixuea.courses.mymusic.playback.PlaybackService;
import com.ixuea.courses.mymusic.repository.DefaultRepository;
import com.ixuea.courses.mymusic.util.ListUtil;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.CopyOnWriteArrayList;

import timber.log.Timber;

/**
 * 播放管理器默认实现
 */
public class MusicPlayerManagerImpl implements MusicPlayerManager, AudioManager.OnAudioFocusChangeListener {
    private static MusicPlayerManagerImpl instance;
    private final Context context;
    private final PlaybackRepository playbackRepository;
    private final SuperAudioManager superAudioManager;
    private String uri;
    private Song data;
    private final Object focusLock = new Object();

    /**
     * 播放器监听器
     */
    private CopyOnWriteArrayList<MusicPlayerListener> listeners = new CopyOnWriteArrayList<>();

    /**
     * 音频焦点获取到了，继续播放
     */
    private boolean resumeOnFocusGain;

    /**
     * 是否准备播放了，也就是是否调用了prepare方法
     */
    private boolean isPrepare;

    /**
     * 私有构造方法
     * <p>
     * 这里外部就不能通过new方法来创建对象了
     *
     * @param context
     */
    private MusicPlayerManagerImpl(Context context) {
        //保存context
        //因为后面可能用到
        this.context = context.getApplicationContext();

        //音频管理器
        superAudioManager = SuperAudioManager.getInstance(this.context);

        playbackRepository = PlaybackRepository.getInstance(this.context);
        playbackRepository.addListener(new PlaybackController.Listener() {
            @Override
            public void onPrepared(Song song, long durationMs) {
                //将总进度保存到音乐对象
                song.setDuration(durationMs);

                //旧接口暂时保留MediaPlayer参数；Media3路径下不再暴露旧播放器实例
                ListUtil.eachListener(listeners, musicPlayerListener -> musicPlayerListener.onPrepared(null, song));
            }

            @Override
            public void onPlaying(Song song) {
                ListUtil.eachListener(listeners, musicPlayerListener -> musicPlayerListener.onPlaying(song));
            }

            @Override
            public void onPaused(Song song) {
                ListUtil.eachListener(listeners, musicPlayerListener -> musicPlayerListener.onPaused(song));
            }

            @Override
            public void onProgress(Song song, long positionMs, long durationMs) {
                song.setProgress(positionMs);
                if (durationMs > 0) {
                    song.setDuration(durationMs);
                }

                ListUtil.eachListener(listeners, musicPlayerListener -> musicPlayerListener.onProgress(song));
            }

            @Override
            public void onCompletion(Song song) {
                isPrepare = false;

                ListUtil.eachListener(listeners, listener -> listener.onCompletion(null));
            }

            @Override
            public void onError(Exception exception, Song song) {
                ListUtil.eachListener(listeners, listener -> listener.onError(exception, song));
            }
        });
    }

    /**
     * 获取播放管理器
     * getInstance：方法名可以随便取
     * 只是在Java这边大部分项目都取这个名字
     *
     * @return
     */
    public synchronized static MusicPlayerManager getInstance(Context context) {
        if (instance == null) {
            instance = new MusicPlayerManagerImpl(context);
        }
        return instance;
    }

    @Override
    public void play(String uri, Song data) {
        //保存信息
        this.uri = uri;
        this.data = data;

        //获取音频焦点
        if (!requestAudioFocus()) {
            return;
        }

        playNow();
    }

    private void playNow() {
        isPrepare = true;

        //Media3 prepare是异步路径，不再在调用线程同步阻塞网络媒体
        PlaybackService.start(context);
        playbackRepository.play(uri, data);
        prepareLyric(data);
    }

    public void prepareLyric(Song data) {
        this.data = data;

        //歌词处理
        //真实项目可能会
        //将歌词这个部分拆分到其他组件中
        if (data.getParsedLyric() != null) {
            //通知歌词改变了
            onLyricReady();
        } else if (StringUtils.isNotBlank(data.getLyric())) {
            parseLyric();

            onLyricReady();
        } else {
            if (data.isLocal()) {
                onLyricReady();
            } else {
                DefaultRepository.getInstance()
                        .songDetail(data.getId())
                        .subscribe(new HttpObserver<DetailResponse<Song>>() {
                            @Override
                            public void onSucceeded(DetailResponse<Song> songDetailResponse) {
                                //请求成功
                                if (songDetailResponse != null && songDetailResponse.getData() != null) {
                                    //数据设置歌曲对象
                                    data.setStyle(songDetailResponse.getData().getStyle());
                                    data.setLyric(songDetailResponse.getData().getLyric());

                                    if (StringUtils.isNotBlank(data.getLyric())) {
                                        parseLyric();
                                    }
                                }

                                //通知歌词改变了
                                onLyricReady();
                            }
                        });
            }
        }
    }

    private void parseLyric() {
        data.setParsedLyric(LyricParser.parse(data.getStyle(), data.getLyric()));
    }

    private void onLyricReady() {
        playbackRepository.updateLyric(data, true);

        //不管有没有歌词都要回调
        ListUtil.eachListener(listeners, listener -> listener.onLyricReady(data));
    }

    /**
     * 获取音频焦点
     *
     * @return true：获取成功
     */
    private boolean requestAudioFocus() {
        int audioFocusResult = superAudioManager.requestAudioFocus(this, AudioAttributes.CONTENT_TYPE_MUSIC);
        synchronized (focusLock) {
            if (audioFocusResult != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                //获取失败了，或者稍后才能获取到（拨打电话了）
                resumeOnFocusGain = true;
                return false;
            }
        }
        return true;
    }

    /**
     * 是否没有进度监听器
     *
     * @return
     */
    private boolean isEmptyListeners() {
        return listeners.size() == 0;
    }

    @Override
    public boolean isPlaying() {
        return playbackRepository.isPlaying();
    }

    @Override
    public void pause() {
        if (isPlaying()) {
            //如果在播放就暂停
            playbackRepository.pause();
        }
    }

    @Override
    public void resume() {
        if (!isPlaying()) {
            //获取音频焦点
            if (!requestAudioFocus()) {
                return;
            }

            resumeNow();
        }
    }

    private void resumeNow() {
        //如果没有播放就播放
        PlaybackService.start(context);
        playbackRepository.resume();
    }

    @Override
    public void addMusicPlayerListener(MusicPlayerListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }

        playbackRepository.setProgressUpdatesEnabled(!isEmptyListeners());
    }

    @Override
    public void removeMusicPlayerListener(MusicPlayerListener listener) {
        listeners.remove(listener);
        playbackRepository.setProgressUpdatesEnabled(!isEmptyListeners());
    }

    @Override
    public void seekTo(int progress) {
        playbackRepository.seekTo(progress);
    }

    @Override
    public void setLooping(boolean looping) {
        playbackRepository.setLooping(looping);
    }

    /**
     * 音频焦点改变了回调
     *
     * @param focusChange
     */
    @Override
    public void onAudioFocusChange(int focusChange) {
        Timber.d("onAudioFocusChange %s", focusChange);

        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                //获取到焦点了
                if (resumeOnFocusGain) {
                    if (isPrepare) {
                        resumeNow();
                    } else {
                        playNow();
                    }

                    resumeOnFocusGain = false;
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                //永久失去焦点，例如：其他应用请求时，也是播放音乐
                if (isPlaying()) {
                    pause();
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                //暂时性失去焦点，例如：通话了，或者呼叫了语音助手等请求
                if (isPlaying()) {
                    resumeOnFocusGain = true;
                    pause();
                }
                break;
        }
    }
}
