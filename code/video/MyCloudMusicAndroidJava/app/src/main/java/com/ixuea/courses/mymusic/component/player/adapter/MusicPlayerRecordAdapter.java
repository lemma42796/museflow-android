package com.ixuea.courses.mymusic.component.player.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.ixuea.courses.mymusic.adapter.BaseFragmentStateAdapter;
import com.ixuea.courses.mymusic.component.player.fragment.RecordFragment;
import com.ixuea.courses.mymusic.component.song.model.Song;

/**
 * 黑胶唱片adapter
 */
public class MusicPlayerRecordAdapter extends BaseFragmentStateAdapter<Song> {
    /**
     * 构造方法
     */
    public MusicPlayerRecordAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return RecordFragment.newInstance(getData(position));
    }
}
