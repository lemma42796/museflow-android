package com.ixuea.courses.mymusic.manager;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

/**
 * 音频管理器，主要处理音频焦点获取，释放，音量调整
 * 音频焦点官方文档：https://developer.android.google.cn/guide/topics/media-apps/audio-focus
 */
public class SuperAudioManager {
    private static SuperAudioManager instance;
    private final Context context;
    private final AudioManager audioManager;
    private AudioAttributes playbackAttributes;
    private AudioFocusRequest focusRequest;

    /**
     * 获取音频焦点时用
     */
    private Handler handler = new Handler(Looper.myLooper());

    public SuperAudioManager(Context context) {
        this.context = context;
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public static SuperAudioManager getInstance(Context context) {
        if (instance == null) {
            instance = new SuperAudioManager(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * 获取音频焦点
     *
     * @param onAudioFocusChangeListener 音频焦点改变了回调
     * @param contentType                播放的内容类型
     * @return 获取的焦点结果，外界根据他判断是否开始播放
     */
    public int requestAudioFocus(AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener, int contentType) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //8.0及以上写法
            playbackAttributes = new AudioAttributes.Builder()
                    //用于在播放媒体（音乐，视频）时，获取音频焦点
                    .setUsage(AudioAttributes.USAGE_MEDIA)

                    //播放内容类型
//                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setContentType(contentType)
                    .build();

            //AudioManager.AUDIOFOCUS_GAIN：持续时间未知，也就是不知道要使用多久，表示一直要使用
            focusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(playbackAttributes)
                    .setAcceptsDelayedFocusGain(true)

                    //失去焦点了，要回调我们，而不是默认的自动降低音量
                    .setWillPauseWhenDucked(true)
                    .setOnAudioFocusChangeListener(onAudioFocusChangeListener, handler)
                    .build();

            int result = audioManager.requestAudioFocus(focusRequest);
            return result;
        } else {
            int result = audioManager.requestAudioFocus(onAudioFocusChangeListener,
                    //播放音乐使用
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN);

            return result;
        }
    }
}
