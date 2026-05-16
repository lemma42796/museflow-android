package com.ixuea.courses.mymusic.component.download.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.component.sheet.adapter.SongAdapter;
import com.ixuea.courses.mymusic.component.download.repository.DownloadRepository;
import com.ixuea.courses.mymusic.component.song.model.Song;
import com.ixuea.courses.mymusic.databinding.FragmentDownloadedBinding;
import com.ixuea.courses.mymusic.fragment.BaseViewModelFragment;
import com.ixuea.superui.util.SuperRecyclerViewUtil;

/**
 * 下载完成界面
 */
public class DownloadedFragment extends BaseViewModelFragment<FragmentDownloadedBinding> {
    private SongAdapter adapter;
    private DownloadRepository repository;

    public static DownloadedFragment newInstance() {

        Bundle args = new Bundle();

        DownloadedFragment fragment = new DownloadedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initViews() {
        super.initViews();
        SuperRecyclerViewUtil.initVerticalLinearRecyclerView(binding.list);
    }

    @Override
    protected void initDatum() {
        super.initDatum();
        repository = DownloadRepository.getInstance();

        //创建适配器
        adapter = new SongAdapter(R.layout.item_song, 1, getChildFragmentManager());

        //设置适配器
        binding.list.setAdapter(adapter);

        loadData();
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                getMusicListManager().setDatum(adapter.getData());

                //获取当前点击的音乐
                Song data = (Song) adapter.getItem(position);

                //播放点击的音乐
                getMusicListManager().play(data);

                startMusicPlayerActivity();
            }
        });
    }

    @Override
    protected void loadData(boolean isPlaceholder) {
        super.loadData(isPlaceholder);
        adapter.setNewInstance(repository.findDownloadedSongs(getOrm()));
    }
}
