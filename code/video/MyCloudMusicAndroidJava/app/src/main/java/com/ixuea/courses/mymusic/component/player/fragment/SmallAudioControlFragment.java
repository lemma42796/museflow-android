package com.ixuea.courses.mymusic.component.player.fragment;

import android.os.Bundle;

import com.ixuea.courses.mymusic.activity.BaseLogicActivity;
import com.ixuea.courses.mymusic.component.lyric.model.Line;
import com.ixuea.courses.mymusic.component.song.model.Song;
import com.ixuea.courses.mymusic.databinding.FragmentAudioControlBinding;
import com.ixuea.courses.mymusic.fragment.BaseViewModelFragment;
import com.ixuea.courses.mymusic.manager.MusicPlayerListener;
import com.ixuea.courses.mymusic.manager.MusicPlayerManager;
import com.ixuea.courses.mymusic.service.MusicPlayerService;
import com.ixuea.courses.mymusic.util.Constant;
import com.ixuea.courses.mymusic.util.ImageUtil;
import com.ixuea.courses.mymusic.util.LyricUtil;

/**
 * 小的音频播放音乐fragment
 */
public class SmallAudioControlFragment extends BaseViewModelFragment<FragmentAudioControlBinding> implements MusicPlayerListener {
    private MusicPlayerManager musicPlayerManager;

    public static SmallAudioControlFragment newInstance(Song data) {

        Bundle args = new Bundle();
        args.putParcelable(Constant.DATA, data);

        SmallAudioControlFragment fragment = new SmallAudioControlFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initViews() {
        super.initViews();
        //这一行歌词始终是选中状态
        binding.lyricLine.setLineSelected(true);
    }

    @Override
    protected void initDatum() {
        super.initDatum();
        Song data = extraData();

        //封面
        ImageUtil.show(getHostActivity(), binding.icon, data.getIcon());

        //标题
        binding.title.setText(data.getTitle());

        //初始化音乐播放管理器
        musicPlayerManager = MusicPlayerService.getMusicPlayerManager(getHostActivity().getApplicationContext());
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        // 迷你播放控制器 容器点击
        binding.container.setOnClickListener(v -> ((BaseLogicActivity) getHostActivity()).startMusicPlayerActivity());
    }

    /**
     * 因为外面使用的是ViewPager2控件，所以在这里就能准确的监听当前显示的界面
     */
    @Override
    public void onResume() {
        super.onResume();
        //添加播放管理器监听器
        musicPlayerManager.addMusicPlayerListener(this);

        if (getMusicListManager().getData() != null) {
            //显示歌词数据
            showLyricData(getMusicListManager().getData());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //移除播放管理器监听器
        musicPlayerManager.removeMusicPlayerListener(this);
    }

    @Override
    public void onProgress(Song data) {
        showLyricData(data);
    }

    @Override
    public void onLyricReady(Song data) {
        showLyricData(data);
    }

    private void showLyricData(Song data) {
        if (data.getParsedLyric() == null) {
            binding.lyricLine.setData(null);
        } else {
            int progress = (int) data.getProgress();

            //获取当前时间对应的歌词索引
            int newLineNumber = LyricUtil.getLineNumber(data.getParsedLyric(), progress);

            Line line = data.getParsedLyric().getDatum().get(newLineNumber);

            binding.lyricLine.setData(line);
            binding.lyricLine.setAccurate(data.getParsedLyric().isAccurate());

            if (data.getParsedLyric().isAccurate()) {
                //获取当前时间是该行的第几个字
                int lyricCurrentWordIndex = LyricUtil.getWordIndex(line, progress);

                //获取当前时间该字
                //已经播放的时间
                float wordPlayedTime = LyricUtil.getWordPlayedTime(line, progress);

                //将当前时间对应的字索引设置到控件
                binding.lyricLine.setLyricCurrentWordIndex(lyricCurrentWordIndex);

                //设置当前字已经播放的时间
                binding.lyricLine.setWordPlayedTime(wordPlayedTime);

                //刷新控件
                binding.lyricLine.onProgress();
            }
        }
    }
}
