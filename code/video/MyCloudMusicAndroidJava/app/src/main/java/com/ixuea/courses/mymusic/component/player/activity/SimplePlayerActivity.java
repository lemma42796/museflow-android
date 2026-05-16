package com.ixuea.courses.mymusic.component.player.activity;

import android.media.MediaPlayer;
import android.view.View;
import android.widget.SeekBar;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.activity.BaseTitleActivity;
import com.ixuea.courses.mymusic.component.player.adapter.SimplePlayerAdapter;
import com.ixuea.courses.mymusic.component.song.model.Song;
import com.ixuea.courses.mymusic.databinding.ActivitySimplePlayerBinding;
import com.ixuea.courses.mymusic.manager.MusicPlayerListener;
import com.ixuea.courses.mymusic.manager.MusicPlayerManager;
import com.ixuea.courses.mymusic.service.MusicPlayerService;
import com.ixuea.courses.mymusic.util.PlayListUtil;
import com.ixuea.courses.mymusic.util.SuperDateUtil;

/**
 * 简单的播放器实现
 * 主要测试音乐播放相关逻辑
 * 因为黑胶唱片界面的逻辑比较复杂
 * 如果在和播放相关逻辑混一起，不好实现
 * 所以我们可以先使用一个简单的播放器
 * 从而把播放器相关逻辑实现完成
 * 然后在对接的黑胶唱片，就相对来说简单一点
 */
public class SimplePlayerActivity extends BaseTitleActivity<ActivitySimplePlayerBinding> implements MusicPlayerListener, SeekBar.OnSeekBarChangeListener {

    private MusicPlayerManager musicPlayerManager;
    private boolean isSeekTracking;
    private SimplePlayerAdapter adapter;

    @Override
    protected void initDatum() {
        super.initDatum();
        musicPlayerManager = MusicPlayerService.getMusicPlayerManager(getApplicationContext());

//        String songUrl = "http://dev-courses-misuc.ixuea.com/assets/s1.mp3";
//
//        Song song = new Song();
//        song.setUri(songUrl);
//
//        //播放音乐
//        musicPlayerManager.play(songUrl, song);

        adapter = new SimplePlayerAdapter(android.R.layout.simple_list_item_1);
        binding.list.setAdapter(adapter);

        adapter.setNewInstance(getMusicListManager().getDatum());


    }

    /**
     * 选中当前音乐
     */
    private void scrollPosition() {
        //选中当前播放的音乐

        //使用post方法是
        //将方法放到了消息循环
        //如果不这样做
        //在onCreate这样的方法中滚动无效
        //因为这时候列表的数据还没有显示完成
        //具体的这部分我们在《详解View》课程中讲解了
        binding.list.post(new Runnable() {
            @Override
            public void run() {
                //获取当前音乐的位置
                int index = getMusicListManager().getDatum().indexOf(getMusicListManager().getData());

                if (index != -1) {
                    //滚动到该位置
                    binding.list.smoothScrollToPosition(index);

                    //选中
                    adapter.setSelectedIndex(index);
                }
            }
        });
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        //设置item点击事件
        adapter.setOnItemClickListener((adapter, view, position) -> {
            //获取这一首音乐
            Song data = getMusicListManager().getDatum().get(position);

            //播放音乐
            getMusicListManager().play(data);
        });

        //设置拖拽进度控件监听器
        binding.progress.setOnSeekBarChangeListener(this);

        //上一曲点击
        binding.previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMusicListManager().play(getMusicListManager().previous());
            }
        });

        //播放点击
        binding.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playOrPause();
            }
        });

        //下一曲点击
        binding.next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取下一首音乐
                Song data = getMusicListManager().next();

                getMusicListManager().play(data);
            }
        });

        //循环模式点击
        binding.loopModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //更改循环模式
                getMusicListManager().changeLoopModel();

                //显示循环模式
                showLoopModel();
            }
        });
    }

    private void showLoopModel() {
        PlayListUtil.showLoopModel(getMusicListManager().getLoopModel(), binding.loopModel);
    }

    /**
     * 播放或暂停
     */
    private void playOrPause() {
        if (musicPlayerManager.isPlaying()) {
            getMusicListManager().pause();
        } else {
            getMusicListManager().resume();
        }
    }

    /**
     * 显示时长
     */
    private void showDuration() {
        int end = (int) getMusicListManager().getData().getDuration();

        //格式为分钟:秒
        String endString = SuperDateUtil.ms2ms(end);
        binding.end.setText(endString);

        //设置到进度条
        binding.progress.setMax(end);
    }

    private void showLyricData() {
        binding.lyricList.setData(getMusicListManager().getData().getParsedLyric());
    }

    @Override
    public void onPrepared(MediaPlayer mp, Song data) {
        //显示初始化数据
        showInitData();

        //显示时长
        showDuration();

        //选中当前播放的音乐
        scrollPosition();
    }

    @Override
    public void onProgress(Song data) {
        if (isSeekTracking) {
            return;
        }

        showProgress();
    }

    /**
     * 显示播放进度
     */
    private void showProgress() {
        int progress = (int) getMusicListManager().getData().getProgress();

        //格式化进度
        binding.start.setText(SuperDateUtil.ms2ms(progress));

        binding.progress.setProgress(progress);

        //显示歌词进度
        binding.lyricList.setProgress(progress);
    }

    /**
     * 显示初始化数据
     */
    private void showInitData() {
        //获取当前播放的音乐
        Song data = getMusicListManager().getData();

        //显示标题
        setTitle(data.getTitle());
    }

    /**
     * 显示音乐播放状态
     */
    private void showMusicPlayStatus() {
        if (musicPlayerManager.isPlaying()) {
            showPauseStatus();
        } else {
            showPlayStatus();
        }
    }


    /**
     * 界面可见了
     */
    @Override
    protected void onResume() {
        super.onResume();

        //显示初始化数据
        showInitData();

        //显示音乐时长
        showDuration();

        //显示播放进度
        showProgress();

        //显示播放状态
        showMusicPlayStatus();

        //显示循环模式
        showLoopModel();

        //选中当前播放的音乐
        scrollPosition();

        //显示歌词数据
        showLyricData();

        //设置播放监听器
        musicPlayerManager.addMusicPlayerListener(this);
    }

    /**
     * 界面不可见了
     */
    @Override
    protected void onPause() {
        super.onPause();
        //取消播放监听器
        musicPlayerManager.removeMusicPlayerListener(this);
    }

    @Override
    public void onPaused(Song data) {
        showPlayStatus();
    }

    @Override
    public void onPlaying(Song data) {
        showPauseStatus();
    }

    @Override
    public void onLyricReady(Song data) {
        showLyricData();
    }

    /**
     * 显示播放状态
     */
    private void showPlayStatus() {
        binding.play.setText(R.string.play);
    }

    /**
     * 显示暂停状态
     */
    private void showPauseStatus() {
        binding.play.setText(R.string.pause);
    }

    /**
     * 进度条改变了
     *
     * @param seekBar
     * @param progress 当前改变后的进度
     * @param fromUser 是否是用户触发的
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            //跳转到该位置播放
            getMusicListManager().seekTo(progress);
        }
    }

    /**
     * 开始拖拽进度条
     *
     * @param seekBar
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isSeekTracking = true;
    }

    /**
     * 停止拖拽进度条
     *
     * @param seekBar
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        isSeekTracking = false;
    }
}