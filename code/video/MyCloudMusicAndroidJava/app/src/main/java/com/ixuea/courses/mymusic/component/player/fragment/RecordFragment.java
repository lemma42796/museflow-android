package com.ixuea.courses.mymusic.component.player.fragment;

import android.os.Bundle;
import android.view.View;

import com.ixuea.courses.mymusic.component.player.model.event.RecordClickEvent;
import com.ixuea.courses.mymusic.component.song.model.Song;
import com.ixuea.courses.mymusic.databinding.FragmentRecordBinding;
import com.ixuea.courses.mymusic.fragment.BaseViewModelFragment;
import com.ixuea.courses.mymusic.manager.MusicPlayerListener;
import com.ixuea.courses.mymusic.manager.MusicPlayerManager;
import com.ixuea.courses.mymusic.service.MusicPlayerService;
import com.ixuea.courses.mymusic.util.Constant;
import com.ixuea.courses.mymusic.util.ImageUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * 音乐黑胶唱片界面
 */
public class RecordFragment extends BaseViewModelFragment<FragmentRecordBinding> implements MusicPlayerListener {

    private Song data;
    private MusicPlayerManager musicPlayerManager;

    public static RecordFragment newInstance(Song data) {

        Bundle args = new Bundle();
        args.putParcelable(Constant.DATA, data);

        RecordFragment fragment = new RecordFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initDatum() {
        super.initDatum();
        data = extraData();

        //显示封面
        ImageUtil.show(getHostActivity(), binding.record.binding.icon, data.getIcon());

        musicPlayerManager = MusicPlayerService.getMusicPlayerManager(getHostActivity().getApplicationContext());
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        //黑胶唱片点击事件
        binding.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new RecordClickEvent());
            }
        });
    }

    /**
     * 界面可见了
     */
    @Override
    public void onResume() {
        super.onResume();
        //设置播放监听器
        musicPlayerManager.addMusicPlayerListener(this);
    }

    /**
     * 界面不可见了
     */
    @Override
    public void onPause() {
        super.onPause();
        //取消播放监听器
        musicPlayerManager.removeMusicPlayerListener(this);
    }

    @Override
    public void onProgress(Song data) {
        if (!data.isRotate()) {
            return;
        }

        binding.record.incrementRotate();
    }
}
