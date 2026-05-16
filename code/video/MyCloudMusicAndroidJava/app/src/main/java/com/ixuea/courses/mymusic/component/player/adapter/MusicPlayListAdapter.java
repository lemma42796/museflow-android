package com.ixuea.courses.mymusic.component.player.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.component.song.model.Song;
import com.ixuea.courses.mymusic.manager.MusicListManager;

/**
 * 播放列表adapter
 */
public class MusicPlayListAdapter extends BaseQuickAdapter<Song, BaseViewHolder> {
    private final MusicListManager musicListManager;

    public MusicPlayListAdapter(int layoutResId, MusicListManager musicListManager) {
        super(layoutResId);
        this.musicListManager = musicListManager;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, Song data) {
        //显示标题
        String title = String.format("%s - %s", data.getTitle(), data.getSinger().getNickname());

        holder.setText(R.id.title, title);

        //处理选中状态
        if (data.getId().equals(musicListManager.getData().getId())) {
            //选中

            //颜色设置为主色调
            holder.setTextColor(R.id.title, getContext().getColor(R.color.primary));
        } else {
            //未选中

            //颜色设置为黑色
            holder.setTextColor(R.id.title, getContext().getColor(R.color.black32));
        }
    }
}
