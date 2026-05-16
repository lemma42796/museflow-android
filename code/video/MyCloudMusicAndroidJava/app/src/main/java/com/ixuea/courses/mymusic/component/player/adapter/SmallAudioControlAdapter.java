package com.ixuea.courses.mymusic.component.player.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.ixuea.courses.mymusic.adapter.BaseFragmentStatePagerAdapter;
import com.ixuea.courses.mymusic.component.player.fragment.SmallAudioControlFragment;
import com.ixuea.courses.mymusic.component.song.model.Song;

/**
 * 小音乐播放控制器ViewPager的Adapter
 */
public class SmallAudioControlAdapter extends BaseFragmentStatePagerAdapter<Song> {
    /***
     *  @param context 上下文
     * @param fm Fragment管理器
     */
    public SmallAudioControlAdapter(Context context, @NonNull FragmentManager fm) {
        super(context, fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return SmallAudioControlFragment.newInstance(getData(position));
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
