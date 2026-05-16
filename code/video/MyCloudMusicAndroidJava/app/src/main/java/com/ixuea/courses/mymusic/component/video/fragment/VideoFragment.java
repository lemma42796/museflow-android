package com.ixuea.courses.mymusic.component.video.fragment;

import static autodispose2.AutoDispose.autoDisposable;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.component.api.HttpObserver;
import com.ixuea.courses.mymusic.component.video.activity.VideoDetailActivity;
import com.ixuea.courses.mymusic.component.video.adapter.VideoAdapter;
import com.ixuea.courses.mymusic.component.video.model.Video;
import com.ixuea.courses.mymusic.databinding.FragmentVideoBinding;
import com.ixuea.courses.mymusic.fragment.BaseViewModelFragment;
import com.ixuea.courses.mymusic.model.response.ListResponse;
import com.ixuea.courses.mymusic.repository.DefaultRepository;

import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;

/**
 * 首页-视频界面
 */
public class VideoFragment extends BaseViewModelFragment<FragmentVideoBinding> {

    private VideoAdapter adapter;

    @Override
    protected void initDatum() {
        super.initDatum();
        adapter = new VideoAdapter(R.layout.item_video);
        binding.list.setAdapter(adapter);

        loadData();
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                Video data = (Video) adapter.getItem(position);
                startActivityExtraId(VideoDetailActivity.class, data.getId());
            }
        });
    }

    @Override
    protected void loadData(boolean isPlaceholder) {
        super.loadData(isPlaceholder);
        DefaultRepository.getInstance()
                .videos(1)
                .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new HttpObserver<ListResponse<Video>>() {
                    @Override
                    public void onSucceeded(ListResponse<Video> data) {
                        adapter.setNewInstance(data.getData().getData());
                    }
                });
    }

    public static VideoFragment newInstance() {

        Bundle args = new Bundle();

        VideoFragment fragment = new VideoFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
